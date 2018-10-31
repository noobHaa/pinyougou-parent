package dto;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回对象
 *
 * @Author ll
 * @Date 2018/10/30 16:26
 */
public class PageResult implements Serializable {

    //总记录
    private long totalNum;

    //当前页条数
    private List rows;

    public PageResult(long totalNum, List rows) {
        this.totalNum = totalNum;
        this.rows = rows;
    }

    public long getTotalNum() {

        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
