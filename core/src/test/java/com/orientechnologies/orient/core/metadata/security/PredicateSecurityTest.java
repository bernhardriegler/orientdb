package com.orientechnologies.orient.core.metadata.security;

import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.*;

public class PredicateSecurityTest {

  static String DB_NAME = "test";
  static OrientDB orient;
  private ODatabaseSession db;

  @BeforeClass
  public static void beforeClass() {
    orient = new OrientDB("plocal:.", OrientDBConfig.defaultConfig());
  }

  @AfterClass
  public static void afterClass() {
    orient.close();
  }

  @Before
  public void before() {
    orient.create("test", ODatabaseType.MEMORY);
    this.db = orient.open(DB_NAME, "admin", "admin");
  }

  @After
  public void after() {
    this.db.close();
    orient.drop("test");
    this.db = null;
  }

  @Test
  public void testSqlRead() {
    OSecurityInternal security = ((ODatabaseInternal) db).getSharedContext().getSecurity();

    db.createClass("Person");

    OSecurityPolicy policy = security.createSecurityPolicy(db, "testPolicy");
    policy.setActive(true);
    policy.setReadRule("name = 'foo'");
    security.saveSecurityPolicy(db, policy);
    security.setSecurityPolicy(db, security.getRole(db, "reader"), "database.class.Person", policy);

    OElement elem = db.newElement("Person");
    elem.setProperty("name", "foo");
    db.save(elem);

    elem = db.newElement("Person");
    elem.setProperty("name", "bar");
    db.save(elem);

    db.close();
    this.db = orient.open(DB_NAME, "reader", "reader");
    OResultSet rs = db.query("select from Person");
    Assert.assertTrue(rs.hasNext());
    rs.next();
    Assert.assertFalse(rs.hasNext());
    rs.close();
  }

  @Test
  public void testSqlReadWithIndex() {
    OSecurityInternal security = ((ODatabaseInternal) db).getSharedContext().getSecurity();

    OClass person = db.createClass("Person");
    person.createProperty("name", OType.STRING);
    db.command("create index Person.name on Person (name) NOTUNIQUE");

    OSecurityPolicy policy = security.createSecurityPolicy(db, "testPolicy");
    policy.setActive(true);
    policy.setReadRule("name = 'foo'");
    security.saveSecurityPolicy(db, policy);
    security.setSecurityPolicy(db, security.getRole(db, "reader"), "database.class.Person", policy);

    OElement elem = db.newElement("Person");
    elem.setProperty("name", "foo");
    db.save(elem);

    elem = db.newElement("Person");
    elem.setProperty("name", "bar");
    db.save(elem);

    db.close();
    this.db = orient.open(DB_NAME, "reader", "reader");
    OResultSet rs = db.query("select from Person where name = 'bar'");
    Assert.assertFalse(rs.hasNext());
    rs.close();
  }

  @Test
  public void testSqlReadWithIndex2() {
    OSecurityInternal security = ((ODatabaseInternal) db).getSharedContext().getSecurity();

    OClass person = db.createClass("Person");
    person.createProperty("name", OType.STRING);
    db.command("create index Person.name on Person (name) NOTUNIQUE");

    OSecurityPolicy policy = security.createSecurityPolicy(db, "testPolicy");
    policy.setActive(true);
    policy.setReadRule("surname = 'foo'");
    security.saveSecurityPolicy(db, policy);
    security.setSecurityPolicy(db, security.getRole(db, "reader"), "database.class.Person", policy);

    OElement elem = db.newElement("Person");
    elem.setProperty("name", "foo");
    elem.setProperty("surname", "foo");
    db.save(elem);

    elem = db.newElement("Person");
    elem.setProperty("name", "foo");
    elem.setProperty("surname", "bar");
    db.save(elem);

    db.close();
    this.db = orient.open(DB_NAME, "reader", "reader");
    OResultSet rs = db.query("select from Person where name = 'foo'");
    Assert.assertTrue(rs.hasNext());
    OResult item = rs.next();
    Assert.assertEquals("foo", item.getProperty("surname"));
    Assert.assertFalse(rs.hasNext());
    rs.close();
  }
}