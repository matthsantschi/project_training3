/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.module;

import ch.bfh.ti.academia.util.ConnectionManager;
import ch.bfh.ti.academia.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

/**
 * The class ModulesServlet provides REST endpoints for the administration of modules.
 */
@WebServlet("/api/modules/*")
public class ModuleController extends HttpServlet {

	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String JSON_MEDIA_TYPE = "application/json";

	private static final Logger logger = Logger.getLogger(ModuleRepository.class.getName());

	private final ConnectionManager connectionManager = ConnectionManager.getInstance();
	private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Connection connection = connectionManager.getConnection();
		ModuleRepository repository = new ModuleRepository(connection);
		try {
			if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
				logger.info("Getting all modules");
				List<Module> modules = repository.findAll();
				response.setStatus(SC_OK);
				response.setHeader(CONTENT_TYPE_HEADER, JSON_MEDIA_TYPE);
				objectMapper.writeValue(response.getOutputStream(), modules);
			} else {
				String nr = request.getPathInfo().substring(1);
				logger.info("Getting module " + nr);
				Module module = repository.find(nr);
				if (module == null) {
					connectionManager.rollback(connection);
					response.sendError(SC_NOT_FOUND);
					return;
				}
				response.setStatus(SC_OK);
				response.setHeader(CONTENT_TYPE_HEADER, JSON_MEDIA_TYPE);
				objectMapper.writeValue(response.getOutputStream(), module);
			}
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		} finally {
			connectionManager.close(connection);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Connection connection = connectionManager.getConnection();
		ModuleRepository repository = new ModuleRepository(connection);
		try {
			Module module = objectMapper.readValue(request.getInputStream(), Module.class);
			if (repository.find(module.getNr()) != null) {
				response.sendError(SC_CONFLICT);
				return;
			}
			logger.info("Adding module " + module.getNr());
			repository.persist(module);
			response.setStatus(SC_CREATED);
			connectionManager.commit(connection);
		} catch (SQLException ex) {
			connectionManager.rollback(connection);
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		} finally {
			connectionManager.close(connection);
		}
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Connection connection = connectionManager.getConnection();
		ModuleRepository repository = new ModuleRepository(connection);
		try {
			String pathInfo = request.getPathInfo();
			if (pathInfo == null || pathInfo.equals("/")) {
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				return;
			}
			String nr = pathInfo.substring(1);
			Module module = objectMapper.readValue(request.getInputStream(), Module.class);
			if (module.getNr() == null || !module.getNr().equals(nr)) {
				response.sendError(SC_BAD_REQUEST);
				return;
			}
			logger.info("Updating module " + nr);
			boolean success = repository.update(module);
			response.setStatus(success ? SC_NO_CONTENT : SC_NOT_FOUND);
			connectionManager.commit(connection);
		} catch (JsonParseException ex) {
			response.sendError(SC_BAD_REQUEST);
		} catch (SQLException ex) {
			connectionManager.rollback(connection);
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		} finally {
			connectionManager.close(connection);
		}
	}

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Connection connection = connectionManager.getConnection();
		ModuleRepository repository = new ModuleRepository(connection);
		try {
			String nr = request.getPathInfo().substring(1);
			logger.info("Deleting module " + nr);
			boolean success = repository.delete(nr);
			response.setStatus(success ? SC_NO_CONTENT : SC_NOT_FOUND);
			connectionManager.commit(connection);
		} catch (SQLException ex) {
			connectionManager.rollback(connection);
			logger.log(Level.SEVERE, ex.getMessage(), ex);
			response.setStatus(SC_INTERNAL_SERVER_ERROR);
		} finally {
			connectionManager.close(connection);
		}
	}
}
