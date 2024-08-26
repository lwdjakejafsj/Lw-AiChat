package io.luowei.aichat.model.sign;

import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName month_sign
 */
@Data
public class MonthSignEntity implements Serializable {
    /**
     * 自增id
     */
    private Long id;

    /**
     * 用户id
     */
    private String openid;

    /**
     * 签到月份，值：2024-02
     */
    private String signMonth;

    /**
     * 记录每个月的签到情况
     */
    private Long signRecord;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        MonthSignEntity other = (MonthSignEntity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOpenid() == null ? other.getOpenid() == null : this.getOpenid().equals(other.getOpenid()))
            && (this.getSignMonth() == null ? other.getSignMonth() == null : this.getSignMonth().equals(other.getSignMonth()))
            && (this.getSignRecord() == null ? other.getSignRecord() == null : this.getSignRecord().equals(other.getSignRecord()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOpenid() == null) ? 0 : getOpenid().hashCode());
        result = prime * result + ((getSignMonth() == null) ? 0 : getSignMonth().hashCode());
        result = prime * result + ((getSignRecord() == null) ? 0 : getSignRecord().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", openid=").append(openid);
        sb.append(", signMonth=").append(signMonth);
        sb.append(", signRecord=").append(signRecord);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}