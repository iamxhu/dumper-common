package com.github.dumper.common.transformer;

import com.google.common.base.Strings;
import com.github.dumper.common.CacheSupport;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-2-20
 * Time: 上午9:27
 */
public class CatCacheSupport implements CacheSupport<String, CatCacheDo> {
    private ConcurrentHashMap<String, CatCacheDo> cid2CatCache = new ConcurrentHashMap<String, CatCacheDo>();
    private ConcurrentHashMap<String, CatCacheDo> fid2CatCache = new ConcurrentHashMap<String, CatCacheDo>();

    private void buildFidNamePath(CatCacheDo catCacheDo) {
        if (!Strings.isNullOrEmpty(catCacheDo.getCatPath())
            && catCacheDo.getCatPath().contains(";")) {
            String[] fids = catCacheDo.getCatPath().split(";");
            StringBuilder listNameCatNameBuilder = new StringBuilder();
            StringBuilder listNamePath = new StringBuilder();
            for (String fid : fids) {
                if (!Strings.isNullOrEmpty(fid)) {
                    CatCacheDo cacheDo = fid2CatCache.get(fid);
                    try {
                        listNameCatNameBuilder.append(";").append(cacheDo.getListName())
                            .append("|").append(cacheDo.getName());
                        listNamePath.append(";").append(cacheDo.getListName());
                    } catch (Exception e) {
                        throw new IllegalStateException();
                    }
                }
            }

            catCacheDo.setListNameCatNamePath(listNameCatNameBuilder.substring(1));
            catCacheDo.setListNamePath(listNamePath.substring(1));
        } else {
            catCacheDo
                .setListNameCatNamePath(catCacheDo.getListName() + "|" + catCacheDo.getName());
            catCacheDo.setListNamePath(catCacheDo.getListName());
        }

    }

    public static final String CAT_SQL   = "select cid,cat_path from item_cats order by cid desc";

    public static final String F_CAT_SQL = "select f.fid,f.name,f.cat_path,f.list_name,"
                                           + "m.mapping_id from item_cat_front f left join "
                                           + "item_cat_mapping m  on f.fid = m.mapping_cat_id and m.type = 'C' order by f.fid ";

    @Override
    public CatCacheDo getValue(String cid) {
        return cid2CatCache.get(cid);
    }

    @Override
    public void init(JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> frontCats = jdbcTemplate.queryForList(F_CAT_SQL);

        //建立cid到fid的直接映射关联关系
        for (Map<String, Object> frontCat : frontCats) {
            CatCacheDo catCacheDo = new CatCacheDo();
            catCacheDo.setCid(String.valueOf(frontCat.get("mapping_id")));
            catCacheDo.setCatPath(String.valueOf(frontCat.get("cat_path")));
            catCacheDo.setFid(String.valueOf(frontCat.get("fid")));
            catCacheDo.setName(String.valueOf(frontCat.get("name")));
            catCacheDo.setListName(String.valueOf(frontCat.get("list_name")));

            if (catCacheDo.getCid() != null) { //存在多个时，取第一次出现的映射关系，即较小的fid
                cid2CatCache.put(catCacheDo.getCid(), catCacheDo);
            }
            fid2CatCache.put(catCacheDo.getFid(), catCacheDo);
        }

        //生成前台类目的路径名称
        for (String cid : cid2CatCache.keySet()) {
            CatCacheDo catCacheDo = cid2CatCache.get(cid);
            buildFidNamePath(catCacheDo);
        }

        List<Map<String, Object>> itemCats = jdbcTemplate.queryForList(CAT_SQL);
        for (Map<String, Object> itemCat : itemCats) {
            String cid = String.valueOf(itemCat.get("cid"));
            CatCacheDo catCacheDo = cid2CatCache.get(cid);
            if (catCacheDo == null) { // 没有cid到fid的直接映射，则取其父级的cid
                String catPath = String.valueOf(itemCat.get("cat_path"));
                String[] cids = catPath.split(";");
                for (int i = cids.length - 1; i > 0; i--) { //从最低层开始往上查找cid
                    catCacheDo = cid2CatCache.get(cids[i]);
                    if (catCacheDo != null) { //如果存在，即是该cid对应的
                        cid2CatCache.put(cid, catCacheDo);
                        break;
                    }
                }
            }
        }

    }

}
