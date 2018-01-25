package com.fox.dynamodb.repositories;

import com.fox.dynamodb.model.QAStatusInfo;
import com.fox.dynamodb.model.QAStatusInfoId;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by shuangf on 1/9/18.
 */

@EnableScan
public interface QAUpdateStatusRepository extends CrudRepository <QAStatusInfo, QAStatusInfoId> {
//    List<QAStatusInfo> findByProject(String project);
}
