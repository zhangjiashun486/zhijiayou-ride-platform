const test = require('node:test');
const assert = require('node:assert/strict');
const path = require('node:path');
const vm = require('node:vm');
const esbuild = require('esbuild');

// 运行真实的 API 模块，只替换无关的 Axios 依赖和浏览器外部环境。
async function loadApi(response) {
  const built = await esbuild.build({
    entryPoints: [path.join(__dirname, '../src/api/ai.js')],
    bundle: true, write: false, platform: 'node', format: 'cjs',
    plugins: [{ name: 'stub-axios', setup(build) {
      build.onResolve({ filter: /^\.\/index$/ }, () => ({ path: 'axios', namespace: 'stub' }));
      build.onLoad({ filter: /.*/, namespace: 'stub' }, () => ({ contents: 'export default {}' }));
    } }],
  });
  const storage = new Map([
    ['SESSION_TOKEN_KEY', 'user-a'], ['currentUser', '{}'], ['currentRole', '0'],
  ]);
  const window = { location: { hash: '' } };
  let sentBody;
  const sandbox = {
    module: { exports: {} }, AbortController, TextDecoder,
    localStorage: { getItem: key => storage.get(key), removeItem: key => storage.delete(key) },
    window,
    fetch: async (_url, options) => { sentBody = JSON.parse(options.body); return response; },
  };
  vm.runInNewContext(built.outputFiles[0].text, sandbox);
  return { api: sandbox.module.exports, storage, window, getSentBody: () => sentBody };
}

test('接收后端会话 ID 后再交付 SSE 文本，不把 ID 当回复', async () => {
  const env = await loadApi(new Response('data:你好\n\ndata:世界\n\n', {
    headers: { 'X-Conversation-Id': 'server-cid', 'Content-Type': 'text/event-stream' },
  }));
  const events = [];
  await new Promise((resolve, reject) => {
    env.api.sendMessageStream({ conversationId: '', message: '你好' },
      token => events.push(['token', token]), resolve, reject,
      cid => events.push(['conversation', cid]));
  });
  assert.deepEqual(events, [['conversation', 'server-cid'], ['token', '你好'], ['token', '世界']]);
  assert.equal(env.getSentBody().conversationId, '');
});

test('401 清理登录信息并引导重新登录', async () => {
  const env = await loadApi(new Response('', { status: 401 }));
  const error = await new Promise(resolve => {
    env.api.sendMessageStream({ message: '你好' },
      () => assert.fail('不应收到回复'), () => assert.fail('不应成功'), resolve);
  });
  assert.equal(error.message, 'HTTP 401');
  assert.equal(env.storage.size, 0);
  assert.equal(env.window.location.hash, '#/login');
});

test('403 报告无权访问并保留当前用户登录状态', async () => {
  const env = await loadApi(new Response('', { status: 403 }));
  const error = await new Promise(resolve => {
    env.api.sendMessageStream({ conversationId: 'other-cid', message: '你好' },
      () => assert.fail('不应收到回复'), () => assert.fail('不应成功'), resolve,
      () => assert.fail('不应更新会话 ID'));
  });
  assert.equal(error.message, 'HTTP 403');
  assert.equal(env.storage.get('SESSION_TOKEN_KEY'), 'user-a');
  assert.equal(env.window.location.hash, '');
});
