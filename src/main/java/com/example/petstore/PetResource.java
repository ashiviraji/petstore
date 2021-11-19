package com.example.petstore;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.vertx.ext.web.codec.BodyCodec;
import org.json.JSONException;
import org.json.JSONObject;


import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/pets")
@Produces("application/json")
public class PetResource {

	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "All Pets", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(ref = "Pet"))) })
	@GET
	public Response getPets() {

		return Response.ok(PetDataSet.getInstance().getArrayList()).build();
	}

	@APIResponses(value = {
			@APIResponse(responseCode = "200", description = "Pet for id", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(ref = "Pet"))),
			@APIResponse(responseCode = "404", description = "No Pet found for the id.") })
	@GET
	@Path("{petId}")
	public Response getPet(@PathParam("petId") int petId) {
		if (petId < 0) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Pet pet = new Pet();
		pet.setPetId(petId);
		pet.setPetAge(3);
		pet.setPetName("Buula");
		pet.setPetType("Dog");

		return Response.ok(pet).build();
		
	}


	@Path("/add")
	@Produces("application/json")
	@POST
	public Response addPet(String request) throws JSONException {
		JSONObject jsonObject = new JSONObject(request);
		if(jsonObject.has("name") && jsonObject.has("type") && jsonObject.has("age")){
			Pet petData= new Pet();
			petData.setPetName(jsonObject.getString("name"));
			petData.setPetAge(Integer.parseInt(jsonObject.getString("age")));
			petData.setPetType(jsonObject.getString("type"));

			petData.setPetId(PetDataSet.getInstance().getArrayList().get(PetDataSet.getInstance().getArrayList().size()-1).getPetId()+1);
			List<Pet> petList= new ArrayList<Pet>();
			List<Pet> temp = new ArrayList<Pet>();

			petList.add(petData);
			temp.addAll(PetDataSet.getInstance().getArrayList());
			temp.addAll(petList);
			PetDataSet.getInstance().setArrayList(temp);

			return Response.ok(petData).build();

		}else{
			return Response.ok("Error").build();
		}
	}


	@Path("/search")
	@Produces("application/json")
	@GET
	public Response searchPet(@DefaultValue("-1") @QueryParam("id") int petId,
							  @DefaultValue("null") @QueryParam("name") String petName,
							  @DefaultValue("0") @QueryParam("age") int petAge){
		boolean isPetFound = false;
		int id = 0;
		if(petId != -1 && petName.equals("null") && petAge == 0){
			if (petId < 0) {
				return Response.status(Status.NOT_FOUND).build();
			}

			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petId == PetDataSet.getInstance().getArrayList().get(i).getPetId()){
					isPetFound = true;
					id = i;
				}
			}
			if(isPetFound){
				return Response.ok(PetDataSet.getInstance().getArrayList().get(id)).build();
			}else{
				return Response.ok("There is not available pet with id = "+petId).build();
			}
		}else if(petId == -1 && !petName.equals("null") && petAge == 0){
			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petName.equals(PetDataSet.getInstance().getArrayList().get(i).getPetName())){
					isPetFound = true;
					id = i;
				}
			}
			if(isPetFound){
				return Response.ok(PetDataSet.getInstance().getArrayList().get(id)).build();
			}else{
				return Response.ok("There is not available pet with name = "+petName).build();
			}
		}else if(petId == -1 && petName.equals("null") && petAge != 0){
			List<Pet> temp = new ArrayList<Pet>();
			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petAge == PetDataSet.getInstance().getArrayList().get(i).getPetAge()){
					isPetFound = true;
					id = i;
					temp.add(PetDataSet.getInstance().getArrayList().get(id));
				}
			}
			if(isPetFound){
				return Response.ok(temp).build();
			}else{
				return Response.ok("There is not available pet with age = "+petAge).build();
			}
		}else{
			return Response.status(Status.NOT_FOUND).build();
		}


	}



	@DELETE
	@Produces("application/json")
	@Path("/delete/{petId}")
	public Response deletePet(@PathParam("petId") int petId){
		boolean isPetFound = false;
		for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
			if(petId == PetDataSet.getInstance().getArrayList().get(i).getPetId()){
				PetDataSet.getInstance().getArrayList().remove(i);
				isPetFound = true;
			}
		}
		if(isPetFound){
			return Response.ok("{\n" + "\"successful\":true\n" + "}").build();
		}else{
			return Response.ok("{\n" + "\"successful\":false\n" + "}").build();
		}

	}




	@PUT
	@Produces("application/json")
	@Path("/edit/{petId}")
	public Response editPet(@PathParam("petId") int petId,String petData) throws JSONException {
		JSONObject jsonObject = new JSONObject(petData);
		boolean isUpdated = false;
		int id = 0;
		if(jsonObject.has("name")){
			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petId == PetDataSet.getInstance().getArrayList().get(i).getPetId()){
					PetDataSet.getInstance().getArrayList().get(i).setPetName(jsonObject.getString("name"));
					isUpdated=true;
					id = i;
				}
			}
		}
		if(jsonObject.has("age")){
			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petId == PetDataSet.getInstance().getArrayList().get(i).getPetId()){
					PetDataSet.getInstance().getArrayList().get(i).setPetAge(Integer.parseInt(jsonObject.getString("age")));
					isUpdated=true;
					id = i;
				}
			}
		}
		if(jsonObject.has("type")){
			for (int i=0;i<PetDataSet.getInstance().getArrayList().size();i++){
				if(petId == PetDataSet.getInstance().getArrayList().get(i).getPetId()){
					PetDataSet.getInstance().getArrayList().get(i).setPetType(jsonObject.getString("type"));
					isUpdated=true;
					id = i;
				}
			}
		}
		if(isUpdated){
			return Response.ok(PetDataSet.getInstance().getArrayList().get(id)).build();
		}else{
			return Response.ok("{\n" + "\"success\":false\n" + "}").build();
		}

	}



}
