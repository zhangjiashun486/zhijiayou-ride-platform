package com.zhijiaoyou.notice.service;


import com.zhijiaoyou.modules.po.NoticePO;
import com.zhijiaoyou.modules.vo.NoticeVO;

import java.util.List;

public interface NoticeService {

    //存储消息到mongoDB
    public void addNotice(NoticePO message);

    //根据用户accountId查询其未读消息，并在查询后，置为已读
    public List<NoticePO> getNoticeByAccountIds(List<String> accountIds);

    //根据noticeVO中的收发人id查询对话清单，最近的20条
    List<NoticePO> queryList(NoticeVO noticeVO);
}
