package com.eInsight.common.dao.impl;

import com.eInsight.common.Initializer;
import com.eInsight.common.dao.ConsoleDAOImpl;
import com.eInsight.common.dao.MongoDBFactory;
import com.mongodb.DBCollection;

/**
 {
 "_id" : ObjectId("59716de733781b6abeba839d"),
 "content" : {
 "statusCode" : "200",
 "partitionKey" : NumberInt(1),
 "creationDate" : "2017-07-15 00:38:32",
 "outboundData" : "{\"op_status\":\"SUCCESS\",\"operation\":{\"op_name\":\"SETUP_APM_C2C\",\"op_data\":{\"piapp_link\":\"address/piapp/psp/v2?flowid=1AB8C508000000012A29E7E5&opid=F2D93B8B000000012A2CAB9D\"}},\"bizflow\":{\"flow_name\":\"SETUP_APM_C2C\",\"flow_state\":\"INITIATED\",\"creation_date\":\"2017-07-15 00:38:31\",\"lastupdate_date\":\"2017-07-15 00:38:31\",\"flowdata\":{\"piapp_link\":\"address/piapp/psp/v2?flowid=1AB8C508000000012A29E7E5&opid=F2D93B8B000000012A2CAB9D\",\"is_ramp_up\":\"true\",\"user_id\":\"1342620458\",\"billing_currency\":\"USD\",\"account_suffix_id\":\"001\",\"site_id\":0,\"instrument_attr\":\"Primary\"}},\"links\":[{\"ref\":\"flow\",\"href\":\"address:8080/pasvc/v1/_flow/1AB8C508000000012A29E7E5\"},{\"ref\":\"self\",\"href\":\"address:8080/pasvc/v1/_op/F2D93B8B000000012A2CAB9D\"},{\"ref\":\"next\",\"href\":\"address/pasvc/v1/flow/1AB8C508000000012A29E7E5/attach_temp_instrumentid\"},{\"ref\":\"next\",\"href\":\"address/pasvc/v1/flow/1AB8C508000000012A29E7E5/init_paypal_ba\"}],\"responseStatus\":\"OK\",\"flowId\":\"1AB8C508000000012A29E7E5\",\"opId\":\"F2D93B8B000000012A2CAB9D\"}",
 "operationName" : "SETUP_APM_C2C",
 "domain" : NumberInt(0),
 "state" : "INITIATED",
 "auditId" : NumberLong(5003017416),
 "lastModifiedDate" : "2017-07-15 00:38:32",
 "inboundData" : "{\"client\":\"BILLING\",\"dedupeId\":\"15001043111399\",\"flowId\":\"_flow\",\"opName\":\"SETUP_APM_C2C\",\"requestBody\":\"{\\\"userId\\\":\\\"1342620458\\\",\\\"siteId\\\":0,\\\"redirectUrl\\\":\\\"address\\\",\\\"instrumentAttr\\\":\\\"Primary\\\",\\\"additional\\\":{\\\"title\\\":\\\"Billing Autopayment Method\\\"}}\",\"uriPrefix\":\"address:8080/pasvc/v1\"}",
 "flowName" : "SETUP_APM_C2C",
 "clientId" : NumberInt(1),
 "flowId" : NumberLong(5002356709),
 "operationId" : NumberLong(5002537885)
 },
 "url" : "http://address/mmpm/audit/pas/2017-07-15.00-38-00/2017-07-15.00-39-00/all",
 "CreationDate" : ISODate("2017-07-14T16:38:00.000+0000")
 }
 */
public class RawDataDAOImpl extends ConsoleDAOImpl {
    private static final String CONFIG_COLLECTIONNAME = Initializer.EVENT_COLLECTIONNAME;
    private DBCollection rawDataDAO = null;
    private static volatile RawDataDAOImpl instance = null;
    private RawDataDAOImpl() {}

    public static RawDataDAOImpl getRawDataDAOInstance() {
        if (instance == null) {
            synchronized (RawDataDAOImpl.class) {
                if (instance == null) {
                    instance = new RawDataDAOImpl();
                }
            }
        }
        return instance;
    }

    public DBCollection getDAOCollection() {
        if (this.rawDataDAO == null) {
            synchronized (this) {
                if (this.rawDataDAO == null) {
                    this.rawDataDAO = MongoDBFactory.getCollection(CONFIG_COLLECTIONNAME);
                }
            }
        }
        return this.rawDataDAO;
    }
}
