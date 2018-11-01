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
    private long total;

    //当前页条数
    private List rows;

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
