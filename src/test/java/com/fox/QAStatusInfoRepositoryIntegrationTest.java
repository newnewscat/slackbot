package com.fox;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.xspec.L;
import com.fox.dynamodb.model.QAStatusInfo;
import com.fox.dynamodb.model.QAStatusInfoId;
import com.fox.dynamodb.repositories.QAUpdateStatusRepository;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SlackbotApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
@TestPropertySource(properties = { "amazon.dynamodb.endpoint=http://localhost:8000/", "amazon.dynamodb.region = us-west-2", "amazon.aws.accesskey=test1", "amazon.aws.secretkey=test231" })
public class QAStatusInfoRepositoryIntegrationTest {

	private DynamoDBMapper dynamoDBMapper;

	private DynamoDB dynamoDB;


	@Autowired
	private AmazonDynamoDB amazonDynamoDB;

	@Autowired
	QAUpdateStatusRepository repository;

	private static final String TEST_USER = "TEST_USER";
	private static final String KEY_NAME = "Project";
	private static final String SORT_NAME = "TimeStamp";
	private static final Set<String> TEST_UPDATE = new HashSet<>(Arrays.asList("TEST_UPDATE"));


    private String startTime = String.valueOf((new Date(System.currentTimeMillis()-24*60*60*1000)).getTime());

	@Before
	public void setup() throws Exception{
		try{
//			dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

			dynamoDB = new DynamoDB(amazonDynamoDB);

//			Table oldTable = dynamoDB.getTable("QAStatusInfo");
//
//			oldTable.delete();
//
//			oldTable.waitForDelete();

			List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
			attributeDefinitions.add(new AttributeDefinition().withAttributeName(KEY_NAME).withAttributeType("S"));
			attributeDefinitions.add(new AttributeDefinition().withAttributeName(SORT_NAME).withAttributeType("S"));
			List<KeySchemaElement> keySchemaElements = new ArrayList<>();
			keySchemaElements.add(new KeySchemaElement().withAttributeName(KEY_NAME).withKeyType(KeyType.HASH));
			keySchemaElements.add(new KeySchemaElement().withAttributeName(SORT_NAME).withKeyType(KeyType.RANGE));

//			CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(QAStatusInfo.class).withKeySchema(keySchemaElements).withAttributeDefinitions(attributeDefinitions);
//			tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
//			amazonDynamoDB.createTable(tableRequest);

			CreateTableRequest request = new CreateTableRequest()
					.withTableName("QAStatusInfo")
					.withKeySchema(keySchemaElements)
					.withAttributeDefinitions(attributeDefinitions)
					.withProvisionedThroughput(new ProvisionedThroughput()
							.withReadCapacityUnits(5L)
							.withWriteCapacityUnits(5L));

			Table table = dynamoDB.createTable(request);

			table.waitForActive();


		}catch (ResourceInUseException e) {

		}
//		dynamoDBMapper.batchDelete((List<QAStatusInfo>) repository.findAll());
	}

	@Test
	public void getTableInfo(){
		TableDescription tableDescription =
				dynamoDB.getTable("QAStatusInfo").describe();

		System.out.printf("%s: %s \t AttributeDefinations: %s\t KeySchema: %s\t ReadCapacityUnits: %d \t WriteCapacityUnits: %d",
				tableDescription.getTableStatus(),
				tableDescription.getTableName(),
				tableDescription.getAttributeDefinitions().toString(),
				tableDescription.getKeySchema().toString(),
				tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
				tableDescription.getProvisionedThroughput().getWriteCapacityUnits());

	}

	@Test
	public void writeToTableTest(){
		dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
//		QAStatusInfo qaStatusInfo = new QAStatusInfo();
//		qaStatusInfo.setProject("16.2");
//		qaStatusInfo.setUpdate(TEST_UPDATE);
//		qaStatusInfo.setUser(TEST_USER);
////		repository.save(qaStatusInfo);
//		dynamoDBMapper.save(qaStatusInfo);

		QAStatusInfo qaStatusInfo1 = new QAStatusInfo();
		qaStatusInfo1.setProject("Screeners");
		qaStatusInfo1.setUpdate(TEST_UPDATE);
		qaStatusInfo1.setUser("Screeners-user");
//		repository.save(qaStatusInfo);
		dynamoDBMapper.save(qaStatusInfo1);

		List<QAStatusInfo> result = (List<QAStatusInfo>)repository.findAll();

		System.out.println("\tList Project Name "+ result.size());

		QAStatusInfo qaStatusInfo2 = new QAStatusInfo();
		qaStatusInfo2.setProject("Screeners");
		DynamoDBQueryExpression<QAStatusInfo> queryExpression = new DynamoDBQueryExpression<QAStatusInfo>()
				.withHashKeyValues(qaStatusInfo2);
		List<QAStatusInfo> itemList = dynamoDBMapper.query(QAStatusInfo.class, queryExpression);

		for (int i = 0; i < itemList.size(); i++) {
			System.out.println("UdateTime for project 16.2 "+itemList.get(i).getUpdateDate());

		}

		QAStatusInfo qaStatusInfo3 = new QAStatusInfo();
		qaStatusInfo3.setProject("Screeners");
		Condition rangeKeyConditon = new Condition().withComparisonOperator(ComparisonOperator.GT)
				.withAttributeValueList(new AttributeValue().withS(startTime));
		DynamoDBQueryExpression<QAStatusInfo> queryExpression1 = new DynamoDBQueryExpression<QAStatusInfo>()
				.withHashKeyValues(qaStatusInfo3)
				.withRangeKeyCondition("TimeStamp",rangeKeyConditon);
		List<QAStatusInfo> itemList1 = dynamoDBMapper.query(QAStatusInfo.class, queryExpression1);

		for (int i = 0; i < itemList1.size(); i++) {
			System.out.println("User for project Screeners "+itemList1.get(i).getUpdateDate());

		}




	}


//	@Test
//	public void updateTableTest(){
//		dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
//		QAStatusInfo qaStatusInfo = new QAStatusInfo();
//		qaStatusInfo.setProject("16.2");
////		qaStatusInfo.setUpdateDate(updateDate);
//		qaStatusInfo.setUpdate(TEST_UPDATE);
//		qaStatusInfo.setUser(TEST_USER);
////		repository.save(qaStatusInfo);
//		dynamoDBMapper.save(qaStatusInfo);
//
//		List<QAStatusInfo> result = (List<QAStatusInfo>)repository.findAll();
//
//		for(QAStatusInfo info: result){
//			System.out.println("Get table content: "+info.getProject()+info.getUpdate()+info.getUser()+info.getUpdateDate());
//		}
//		assertTrue("Not empty", result.size() > 0);
//		assertTrue("Contains item with expected cost", result.get(0).getUser().equals(TEST_USER));
////		String project =
////		GetItemSpec spec = new GetItemSpec().withPrimaryKey()
//	}


}
