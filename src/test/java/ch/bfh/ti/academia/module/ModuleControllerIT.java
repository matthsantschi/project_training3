/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.module;

import ch.bfh.ti.academia.DBUtil;
import ch.bfh.ti.academia.LogUtil;
import ch.bfh.ti.academia.ServerUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModuleControllerIT {

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	@BeforeAll
	public static void setup() throws Exception {
		RestAssured.port = 8080;
		LogUtil.setLevel(Level.INFO);
		DBUtil.runScript("db-create.sql");
		DBUtil.runScript("db-init.sql");
		ServerUtil.start();
	}

	@AfterAll
	public static void cleanup() throws Exception {
		ServerUtil.stop();
	}

	@Test
	public void addModule() {
		Module module = new Module("M0", "Name0", "Description0");
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD)
				.contentType(ContentType.JSON).accept(ContentType.JSON).body(module)
				.when().post("/api/modules")
				.then().statusCode(201);
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD)
				.contentType(ContentType.JSON).accept(ContentType.JSON).body(module)
				.when().post("/api/modules")
				.then().statusCode(409);
	}

	@Test
	public void getModule() {
		Module foundModule = RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD).accept(ContentType.JSON)
				.when().get("/api/modules/M1")
				.then().statusCode(200).extract().as(Module.class);
		assertEquals("M1", foundModule.getNr());
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD).accept(ContentType.JSON)
				.when().get("/api/modules/Mx")
				.then().statusCode(404);
	}

	@Test
	public void getModules() {
		Module module = new Module("M1", "Name1", "Description1");
		Module[] modules = RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD).accept(ContentType.JSON)
				.when().get("/api/modules")
				.then().statusCode(200).extract().as(Module[].class);
		assertTrue(Arrays.asList(modules).contains(module));
	}

	@Test
	public void updateModule() {
		Module module = RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD).accept(ContentType.JSON)
				.when().get("/api/modules/M2")
				.then().statusCode(200).extract().as(Module.class);
		module.setName("NewName2");
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD)
				.contentType(ContentType.JSON).accept(ContentType.JSON).body(module)
				.when().put("/api/modules/M2")
				.then().statusCode(204);
		Module updatedModule = RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD).accept(ContentType.JSON)
				.when().get("/api/modules/M2")
				.then().statusCode(200).extract().as(Module.class);
		assertEquals(module.getName(), updatedModule.getName());
	}

	@Test
	public void removeModule() {
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD)
				.when().delete("/api/modules/M3")
				.then().statusCode(204);
		RestAssured
				.given().auth().preemptive().basic(USERNAME, PASSWORD)
				.when().delete("/api/modules/M3")
				.then().statusCode(404);
	}
}
