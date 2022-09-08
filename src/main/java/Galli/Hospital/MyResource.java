package Galli.Hospital;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * Root resource (exposed at "Patient" path)
 */
@Path("patients")
public class MyResource {

	private final static String URI_LEADING = "file://";
	private final static String BASE_PATH = "/hospital_files/patients_data/";
	private final static String TRAILING_FILE_NAME = "_private_file";

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to the
	 * client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getPatient(@PathParam("name") String name) throws URISyntaxException, IOException {
		URI fileURI = new URI(URI_LEADING + BASE_PATH + name + TRAILING_FILE_NAME);
		if (Files.exists(Paths.get(fileURI))) {
			byte bytes[] = Files.readAllBytes(Paths.get(fileURI));
			return Response.ok(new String(bytes), MediaType.TEXT_PLAIN).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("patient not in the system").build();
		}
	}

	@POST
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postPatient(@PathParam("name") String name) throws IOException {
		if (createFile(name))
			return Response.status(Status.CREATED).entity(name + " added").build();
		else
			return Response.status(Status.FORBIDDEN).entity("patient record already existing").build();
	}

	private boolean createFile(String name) throws IOException {
		File file = new File(BASE_PATH + name + TRAILING_FILE_NAME);
		if (file.createNewFile()) {
			FileWriter myWriter = new FileWriter(file.getAbsolutePath());
			int fileLines = (int) (Math.round(15000 * Math.random()));
			for (int i = 1; i < fileLines; i++)
				myWriter.write("this is a private medical file \n");
			myWriter.close();
			return true;
		}
		return false;
	}
}
