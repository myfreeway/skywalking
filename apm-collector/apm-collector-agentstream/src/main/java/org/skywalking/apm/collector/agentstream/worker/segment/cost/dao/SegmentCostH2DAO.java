/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.agentstream.worker.segment.cost.dao;

import org.skywalking.apm.collector.core.stream.Data;
import org.skywalking.apm.collector.storage.define.DataDefine;
import org.skywalking.apm.collector.storage.define.segment.SegmentCostTable;
import org.skywalking.apm.collector.storage.h2.SqlBuilder;
import org.skywalking.apm.collector.storage.h2.dao.H2DAO;
import org.skywalking.apm.collector.storage.h2.define.H2SqlEntity;
import org.skywalking.apm.collector.stream.worker.impl.dao.IPersistenceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author peng-yongsheng, clevertension
 */
public class SegmentCostH2DAO extends H2DAO implements ISegmentCostDAO, IPersistenceDAO<H2SqlEntity, H2SqlEntity> {
    private final Logger logger = LoggerFactory.getLogger(SegmentCostH2DAO.class);
    @Override public Data get(String id, DataDefine dataDefine) {
        return null;
    }
    @Override public H2SqlEntity prepareBatchInsert(Data data) {
        logger.debug("segment cost prepareBatchInsert, id: {}", data.getDataString(0));
        H2SqlEntity entity = new H2SqlEntity();
        Map<String, Object> source = new HashMap<>();
        source.put("id", data.getDataString(0));
        source.put(SegmentCostTable.COLUMN_SEGMENT_ID, data.getDataString(1));
        source.put(SegmentCostTable.COLUMN_APPLICATION_ID, data.getDataInteger(0));
        source.put(SegmentCostTable.COLUMN_SERVICE_NAME, data.getDataString(2));
        source.put(SegmentCostTable.COLUMN_COST, data.getDataLong(0));
        source.put(SegmentCostTable.COLUMN_START_TIME, data.getDataLong(1));
        source.put(SegmentCostTable.COLUMN_END_TIME, data.getDataLong(2));
        source.put(SegmentCostTable.COLUMN_IS_ERROR, data.getDataBoolean(0));
        source.put(SegmentCostTable.COLUMN_TIME_BUCKET, data.getDataLong(3));
        logger.debug("segment cost source: {}", source.toString());

        String sql = SqlBuilder.buildBatchInsertSql(SegmentCostTable.TABLE, source.keySet());
        entity.setSql(sql);
        entity.setParams(source.values().toArray(new Object[0]));
        return entity;
    }
    @Override public H2SqlEntity prepareBatchUpdate(Data data) {
        return null;
    }
}
