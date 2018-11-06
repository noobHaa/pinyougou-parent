package vo;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import jdk.internal.dynalink.linker.LinkerServices;

import java.io.Serializable;
import java.util.List;

/**
 * @Author ll
 * @Date 2018/11/6 16:35
 */
public class Goods implements Serializable {
    private TbGoods tbGoods;//spu
    private TbGoodsDesc tbGoodsDesc;//spu
    private List<TbItem> tbItems;//sku

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getTbItems() {
        return tbItems;
    }

    public void setTbItems(List<TbItem> tbItems) {
        this.tbItems = tbItems;
    }
}
