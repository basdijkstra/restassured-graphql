import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.junit.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BasicGraphQLTest {

    @Test
    public void getCompanyData_checkCeo_shouldBeElonMusk() {

        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("{ company { name ceo coo } }");

        given().
            contentType(ContentType.JSON).
            body(query).
        when().
            post("https://api.spacex.land/graphql/").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.company.ceo", equalTo("Elon Musk"));
    }

    @Test
    public void getLaunches_checkMissionName_shouldBeThaicom6_usingJSONObject() {

        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("query getLaunches($limit: Int!){ launches(limit: $limit) { mission_name } }");

        JSONObject variables = new JSONObject();
        variables.put("limit", 10);

        query.setVariables(variables.toString());

        given().
            contentType(ContentType.JSON).
            body(query).
        when().
            post("https://api.spacex.land/graphql/").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.launches[0].mission_name", equalTo("Thaicom 6"));
    }

    @Test
    public void getLaunches_checkMissionName_shouldBeThaicom6_usingPOJO() {

        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("query getLaunches($limit: Int!){ launches(limit: $limit) { mission_name } }");

        QueryLimit queryLimit = new QueryLimit(10);
        query.setVariables(queryLimit);

        given().
            contentType(ContentType.JSON).
            body(query).
        when().
            post("https://api.spacex.land/graphql/").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.launches[0].mission_name", equalTo("Thaicom 6"));
    }

    @Test
    public void addUser_checkReturnedData_shouldCorrespondToDataSent() {

        GraphQLQuery query = new GraphQLQuery();
        query.setQuery("mutation insert_users ($id: uuid!, $name: String!, $rocket: String!) { insert_users(objects: {id: $id, name: $name, rocket: $rocket}) { returning { id name rocket } } }");

        User myUser = new User(
                UUID.randomUUID(),
                "Bas",
                "My awesome rocket"
        );
        query.setVariables(myUser);

        given().
            contentType(ContentType.JSON).
            body(query).
        when().
            post("https://api.spacex.land/graphql/").
        then().
            assertThat().
            statusCode(200).
        and().
            body("data.insert_users.returning[0].id", equalTo(myUser.getId().toString())).
            body("data.insert_users.returning[0].name", equalTo(myUser.getName())).
            body("data.insert_users.returning[0].rocket", equalTo(myUser.getRocket()));
    }
}
