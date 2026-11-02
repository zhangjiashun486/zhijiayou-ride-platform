package com.ruoyi.hitch.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hitch.mapper.VehicleMapper;
import com.ruoyi.hitch.domain.Vehicle;
import com.ruoyi.hitch.service.IVehicleService;
import com.ruoyi.common.core.text.Convert;

/**
 * 车辆信息Service业务层处理
 * 
 * @author Shawn
 * @date 2026-08-13
 */
@Service
public class VehicleServiceImpl implements IVehicleService 
{
    @Autowired
    private VehicleMapper vehicleMapper;

    /**
     * 查询车辆信息
     * 
     * @param id 车辆信息主键
     * @return 车辆信息
     */
    @Override
    public Vehicle selectVehicleById(String id)
    {
        return vehicleMapper.selectVehicleById(id);
    }

    /**
     * 查询车辆信息列表
     * 
     * @param vehicle 车辆信息
     * @return 车辆信息
     */
    @Override
    public List<Vehicle> selectVehicleList(Vehicle vehicle)
    {
        return vehicleMapper.selectVehicleList(vehicle);
    }

    /**
     * 新增车辆信息
     * 
     * @param vehicle 车辆信息
     * @return 结果
     */
    @Override
    public int insertVehicle(Vehicle vehicle)
    {
        return vehicleMapper.insertVehicle(vehicle);
    }

    /**
     * 修改车辆信息
     * 
     * @param vehicle 车辆信息
     * @return 结果
     */
    @Override
    public int updateVehicle(Vehicle vehicle)
    {
        return vehicleMapper.updateVehicle(vehicle);
    }

    /**
     * 批量删除车辆信息
     * 
     * @param ids 需要删除的车辆信息主键
     * @return 结果
     */
    @Override
    public int deleteVehicleByIds(String ids)
    {
        return vehicleMapper.deleteVehicleByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除车辆信息信息
     * 
     * @param id 车辆信息主键
     * @return 结果
     */
    @Override
    public int deleteVehicleById(String id)
    {
        return vehicleMapper.deleteVehicleById(id);
    }
}
