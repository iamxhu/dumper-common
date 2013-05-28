package com.github.dumper.common.transformer;

import com.github.dumper.common.CacheSupport;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: huxing(xing.hu@me.com)
 * Date: 13-3-1
 * Time: 下午5:16
 */
public class PropCacheSupport implements CacheSupport<String, BasePropDo> {

    private Map<String, BasePropDo> basePropDoCache = new ConcurrentHashMap<String, BasePropDo>();

    @Override
    public BasePropDo getValue(String key) {
        return basePropDoCache.get(key);
    }

    @Override
    public void init(JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(SQL);
        for (Map<String, Object> stringObjectMap : mapList) {
            BasePropDo basePropDo = new BasePropDo();
            basePropDo.setBpid(String.valueOf(stringObjectMap.get("bpid")));
            basePropDo.setName(String.valueOf(stringObjectMap.get("name")));
            basePropDo.setPid(String.valueOf(stringObjectMap.get("pid")));
            basePropDo.setSortOrder((Long) stringObjectMap.get("sort_order"));

            basePropDoCache.put(basePropDo.getPid(), basePropDo);
        }
    }

    public static final String SQL = "select bp.bpid,bp.name,bp.sort_order,p.pid from item_props p left outer join "
                                     + " item_base_prop bp on p.bpid = bp.bpid "
                                     + " where bp.selected = 1";
}
