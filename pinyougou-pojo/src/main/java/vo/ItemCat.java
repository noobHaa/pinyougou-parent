package vo;

import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;

/**
 * 商品类别
 *
 * @Author ll
 * @Date 2018/11/6 14:31
 */
public class ItemCat implements Serializable {
    private TbItemCat tbItemCat;
    private String typeId;

    public TbItemCat getTbItemCat() {
        return tbItemCat;
    }

    public void setTbItemCat(TbItemCat tbItemCat) {
        this.tbItemCat = tbItemCat;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
