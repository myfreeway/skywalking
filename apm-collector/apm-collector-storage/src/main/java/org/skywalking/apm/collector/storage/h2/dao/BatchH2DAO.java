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

package org.skywalking.apm.collector.storage.h2.dao;

import org.skywalking.apm.collector.client.h2.H2ClientException;
import org.skywalking.apm.collector.storage.dao.IBatchDAO;
import org.skywalking.apm.collector.storage.h2.define.H2SqlEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peng-yongsheng
 */
public class BatchH2DAO extends H2DAO implements IBatchDAO {
    private final Logger logger = LoggerFactory.getLogger(BatchH2DAO.class);

    @Override
    public void batchPersistence(List<?> batchCollection) {
        if (batchCollection != null && batchCollection.size() > 0) {
            logger.debug("the batch collection size is {}", batchCollection.size());
            Connection conn = null;
            final Map<String, PreparedStatement> batchSqls = new HashMap<>();
            try {
                conn = getClient().getConnection();
                conn.setAutoCommit(true);
                PreparedStatement ps;
                for (Object entity : batchCollection) {
                    H2SqlEntity e = getH2SqlEntity(entity);
                    String sql = e.getSql();
                    if (batchSqls.containsKey(sql)) {
                        ps = batchSqls.get(sql);
                    } else {
                        ps = conn.prepareStatement(sql);
                        batchSqls.put(sql, ps);
                    }

                    Object[] params = e.getParams();
                    if (params != null) {
                        logger.debug("the sql is {}, params size is {}", e.getSql(), params.length);
                        for (int i = 0; i < params.length; i++) {
                            ps.setObject(i + 1, params[i]);
                        }
                    }
                    ps.addBatch();
                }

                for (String k : batchSqls.keySet()) {
                    batchSqls.get(k).executeBatch();
                }
            } catch (SQLException | H2ClientException e) {
                logger.error(e.getMessage(), e);
            }
            batchSqls.clear();
        }
    }

    private H2SqlEntity getH2SqlEntity(Object entity) {
        if (entity instanceof H2SqlEntity) {
            return (H2SqlEntity) entity;
        }
        return null;
    }
}
