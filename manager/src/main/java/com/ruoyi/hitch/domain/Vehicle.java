package com.ruoyi.hitch.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 车辆信息对象 t_vehicle
 * 
 * @author Shawn
 * @date 2026-08-13
 */
public class Vehicle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;

    /** 车牌号 */
    @Excel(name = "车牌号")
    private String carNumber;

    /** 车牌前部照片 */
    @Excel(name = "车牌前部照片")
    private String carFrontPhoto;

    /** 行驶证 */
    @Excel(name = "行驶证")
    private String carBackPhoto;

    /** 人车同框 */
    @Excel(name = "人车同框")
    private String carSidePhoto;

    /** 购车日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "购车日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date purchaseDate;

    /** 所属人手机号码 */
    @Excel(name = "所属人手机号码")
    private String phone;

    /** 认证状态 未认证：0认证成功：1认证失败：2 */
    @Excel(name = "认证状态 未认证：0认证成功：1认证失败：2")
    private Long status;

    /** 乐观锁 */
    @Excel(name = "乐观锁")
    private Long REVISION;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createdBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updatedBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setCarNumber(String carNumber) 
    {
        this.carNumber = carNumber;
    }

    public String getCarNumber() 
    {
        return carNumber;
    }
    public void setCarFrontPhoto(String carFrontPhoto) 
    {
        this.carFrontPhoto = carFrontPhoto;
    }

    public String getCarFrontPhoto() 
    {
        return carFrontPhoto;
    }
    public void setCarBackPhoto(String carBackPhoto) 
    {
        this.carBackPhoto = carBackPhoto;
    }

    public String getCarBackPhoto() 
    {
        return carBackPhoto;
    }
    public void setCarSidePhoto(String carSidePhoto) 
    {
        this.carSidePhoto = carSidePhoto;
    }

    public String getCarSidePhoto() 
    {
        return carSidePhoto;
    }
    public void setPurchaseDate(Date purchaseDate) 
    {
        this.purchaseDate = purchaseDate;
    }

    public Date getPurchaseDate() 
    {
        return purchaseDate;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setREVISION(Long REVISION) 
    {
        this.REVISION = REVISION;
    }

    public Long getREVISION() 
    {
        return REVISION;
    }
    public void setCreatedBy(String createdBy) 
    {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() 
    {
        return createdBy;
    }
    public void setCreatedTime(Date createdTime) 
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() 
    {
        return createdTime;
    }
    public void setUpdatedBy(String updatedBy) 
    {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() 
    {
        return updatedBy;
    }
    public void setUpdatedTime(Date updatedTime) 
    {
        this.updatedTime = updatedTime;
    }

    public Date getUpdatedTime() 
    {
        return updatedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("carNumber", getCarNumber())
            .append("carFrontPhoto", getCarFrontPhoto())
            .append("carBackPhoto", getCarBackPhoto())
            .append("carSidePhoto", getCarSidePhoto())
            .append("purchaseDate", getPurchaseDate())
            .append("phone", getPhone())
            .append("status", getStatus())
            .append("REVISION", getREVISION())
            .append("createdBy", getCreatedBy())
            .append("createdTime", getCreatedTime())
            .append("updatedBy", getUpdatedBy())
            .append("updatedTime", getUpdatedTime())
            .toString();
    }
}
