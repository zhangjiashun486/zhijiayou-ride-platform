package com.ruoyi.hitch.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.hitch.domain.Vehicle;
import com.ruoyi.hitch.service.IVehicleService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 车辆信息Controller
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Controller
@RequestMapping("/hitch/vehicle")
public class VehicleController extends BaseController
{
    private String prefix = "hitch/vehicle";

    @Autowired
    private IVehicleService vehicleService;

    @RequiresPermissions("hitch:vehicle:view")
    @GetMapping()
    public String vehicle()
    {
        return prefix + "/vehicle";
    }

    /**
     * 查询车辆信息列表
     */
    @RequiresPermissions("hitch:vehicle:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Vehicle vehicle)
    {
        startPage();
        List<Vehicle> list = vehicleService.selectVehicleList(vehicle);
        return getDataTable(list);
    }

    /**
     * 导出车辆信息列表
     */
    @RequiresPermissions("hitch:vehicle:export")
    @Log(title = "车辆信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(Vehicle vehicle)
    {
        List<Vehicle> list = vehicleService.selectVehicleList(vehicle);
        ExcelUtil<Vehicle> util = new ExcelUtil<Vehicle>(Vehicle.class);
        return util.exportExcel(list, "车辆信息数据");
    }

    /**
     * 新增车辆信息
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存车辆信息
     */
    @RequiresPermissions("hitch:vehicle:add")
    @Log(title = "车辆信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Vehicle vehicle)
    {
        return toAjax(vehicleService.insertVehicle(vehicle));
    }

    /**
     * 修改车辆信息
     */
    @RequiresPermissions("hitch:vehicle:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap)
    {
        Vehicle vehicle = vehicleService.selectVehicleById(id);
        mmap.put("vehicle", vehicle);
        return prefix + "/edit";
    }

    /**
     * 修改保存车辆信息
     */
    @RequiresPermissions("hitch:vehicle:edit")
    @Log(title = "车辆信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Vehicle vehicle)
    {
        return toAjax(vehicleService.updateVehicle(vehicle));
    }

    /**
     * 删除车辆信息
     */
    @RequiresPermissions("hitch:vehicle:remove")
    @Log(title = "车辆信息", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(vehicleService.deleteVehicleByIds(ids));
    }
}
