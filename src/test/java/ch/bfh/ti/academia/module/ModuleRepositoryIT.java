/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.module;

import ch.bfh.ti.academia.DBUtil;
import ch.bfh.ti.academia.LogUtil;
import ch.bfh.ti.academia.util.ConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModuleRepositoryIT {

	private static Connection connection;
	private static ModuleRepository repository;

	@BeforeAll
	public static void setup() throws SQLException {
		LogUtil.setLevel(Level.FINE);
		DBUtil.runScript("db-create.sql");
		DBUtil.runScript("db-init.sql");
		connection = ConnectionManager.getInstance().getConnection();
		repository = new ModuleRepository(connection);
	}

	@AfterAll
	public static void cleanup() {
		ConnectionManager.getInstance().close(connection);
	}

	@Test
	public void persistModule() throws SQLException {
		Module module = new Module("M0", "Name0", "Description0");
		repository.persist(module);
		assertThrows(SQLException.class, () -> repository.persist(module));
	}

	@Test
	public void findModule() throws SQLException {
		Module foundModule = repository.find("M1");
		assertEquals("M1", foundModule.getNr());
		assertNull(repository.find("Mx"));
	}

	@Test
	public void findModules() throws SQLException {
		List<Module> modules = repository.findAll();
		Module module = new Module("M1", "Name1", "Description1");
		assertTrue(modules.contains(module));
	}

	@Test
	public void updateModule() throws SQLException {
		Module module = repository.find("M2");
		module.setName("NewName2");
		repository.update(module);
		Module updatedModule = repository.find("M2");
		assertEquals(module.getName(), updatedModule.getName());
	}

	@Test
	public void deleteModule() throws SQLException {
		repository.delete("M3");
		assertFalse(repository.delete("M3"));
	}
}
