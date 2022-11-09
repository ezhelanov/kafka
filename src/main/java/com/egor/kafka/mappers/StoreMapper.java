package com.egor.kafka.mappers;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface StoreMapper {

    default Map<String, Object> map(ReadOnlyKeyValueStore<String, Object> storeView) {
        var map = new HashMap<String, Object>();
        if (storeView != null) {
            storeView.all().forEachRemaining(kv -> map.put(kv.key, kv.value));
        }
        return map;
    }

}
