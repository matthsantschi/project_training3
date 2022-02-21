/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The class ModuleRepository provides persistence methods for modules.
 */
public class ModuleRepository {

	private static final String FIND_ALL_QUERY = "SELECT * FROM module";
	private static final String FIND_QUERY = "SELECT * FROM module WHERE nr=?";
	private static final String INSERT_QUERY = "INSERT INTO module (nr, name, description) VALUES (?, ?, ?)";
	private static final String UPDATE_QUERY = "UPDATE module SET name=?, description=? WHERE nr=?";
	private static final String DELETE_QUERY = "DELETE FROM module WHERE nr=?";

	private static final Logger logger = Logger.getLogger(ModuleRepository.class.getName());

	private final Connection connection;

	public ModuleRepository(Connection connection) {
		this.connection = connection;
	}

	public List<Module> findAll() throws SQLException {
		List<Module> modules = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY)) {
			logger.info("Executing query: " + statement);
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				modules.add(getModule(results));
			}
			return modules;
		}
	}

	public Module find(String nr) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(FIND_QUERY)) {
			statement.setString(1, nr);
			logger.info("Executing query: " + statement);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				return getModule(results);
			} else return null;
		}
	}

	public void persist(Module module) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
			int index = 0;
			statement.setString(++index, module.getNr());
			statement.setString(++index, module.getName());
			statement.setString(++index, module.getDescription());
			logger.info("Executing query: " + statement);
			statement.executeUpdate();
		}
	}

	public boolean update(Module module) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
			int index = 0;
			statement.setString(++index, module.getName());
			statement.setString(++index, module.getDescription());
			statement.setString(++index, module.getNr());
			logger.info("Executing query: " + statement);
			return statement.executeUpdate() > 0;
		}
	}

	public boolean delete(String nr) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
			statement.setString(1, nr);
			logger.info("Executing query: " + statement);
			return statement.executeUpdate() > 0;
		}
	}

	private Module getModule(ResultSet results) throws SQLException {
		Module module = new Module();
		module.setNr(results.getString("nr"));
		module.setName(results.getString("name"));
		module.setDescription(results.getString("description"));
		return module;
	}
}
