package com.egor.kafka.mappers;

import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.mapstruct.Mapper;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface StringStoreMapper {

    default Map<String, String> map(ReadOnlyKeyValueStore<String, String> storeView) {
        var map = new HashMap<String, String>();
        storeView.all().forEachRemaining(kv -> map.put(kv.key, kv.value));
        return map;
    }

}
