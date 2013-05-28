package com.github.dumper.common.transformer;

import com.google.common.base.Strings;
import com.github.dumper.common.DumpContext;
import com.github.dumper.common.Transformer;
import com.github.dumper.configure.TransformerConfigure;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-20
 * Time: 上午9:14
 */
public class GoodsTransformer implements Transformer {
    private TransformerConfigure transformerConfigure;

    @Override
    public boolean transform(Map<String, Object> row, DumpContext dumpContext) {
        String cid = String.valueOf(row.get("cid"));
        CatCacheDo cacheDo = (CatCacheDo) dumpContext.getCacheSupport("catCache").getValue(cid);
        if (cacheDo != null) {
            row.put("cat_name", cacheDo.getName());
            row.put("cat_path", cacheDo.getCatPath());
            row.put("list_name_path", cacheDo.getListNamePath());
            row.put("list_name_path_kv", cacheDo.getListNameCatNamePath());
            row.put("list_name", cacheDo.getListName());
            row.put("fid", cacheDo.getFid());
        }

        PropCacheSupport propCache = (PropCacheSupport) dumpContext.getCacheSupport("propCache");
        String props = String.valueOf(row.get("props"));
        String propsStr = String.valueOf(row.get("props_str"));

        Boolean deleted = (Boolean) row.get("deleted");
        if (deleted) {
            row.put("deleted", 1);
        } else {
            row.put("deleted", 0);
        }
        buildPropsKV(props, propsStr, row, propCache);

        return true;
    }

    @Override
    public TransformerConfigure getTransformerConfigure() {
        return this.transformerConfigure;
    }

    @Override
    public void setTransformerConfigure(TransformerConfigure transformerConfigure) {
        this.transformerConfigure = transformerConfigure;
    }

    private void buildPropsKV(String props, String propsStr, Map<String, Object> row,
                              PropCacheSupport propCache) {
        if (Strings.isNullOrEmpty(props) || Strings.isNullOrEmpty(propsStr)) {
            return;
        }

        String[] pIdVids = props.split(";");
        String[] pvStrs = propsStr.split(";");

        int length = pIdVids.length;
        if (pIdVids.length > pvStrs.length) {
            length = pvStrs.length;
        }

        StringBuilder propKvs = new StringBuilder();
        StringBuilder adjustProp = new StringBuilder();
        StringBuilder adjustPropKV = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String pidVid = pIdVids[i];
            String[] pidVidArray = pidVid.split(":");
            if (pidVidArray.length != 2) {
                continue;
            }

            String pid = pidVidArray[0];
            String vid = pidVidArray[1];
            if (i > 0) {
                propKvs.append(";");
            }
            propKvs.append(pIdVids[i]).append("|").append(pvStrs[i]);

            BasePropDo propDo = propCache.getValue(pid);
            if (propDo != null) {
                adjustProp.append(";").append(propDo.getBpid()).append(":").append(vid);
                adjustPropKV.append(";").append(propDo.getBpid()).append(":").append(vid)
                    .append("|").append(pvStrs[i]);
            }
        }

        row.put("props_kv", propKvs.toString());

        if (adjustProp.length() > 0) {
            row.put("base_props", adjustProp.substring(1));
            row.put("base_props_kv", adjustPropKV.substring(1));
        }

    }
}
