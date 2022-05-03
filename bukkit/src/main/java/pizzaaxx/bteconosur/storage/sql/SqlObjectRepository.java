package pizzaaxx.bteconosur.storage.sql;

import net.ibxnjadev.test.Identifiable;
import net.ibxnjadev.test.storage.ObjectRepository;
import net.ibxnjadev.test.storage.query.CompoundQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SqlObjectRepository<O extends Identifiable> implements ObjectRepository<O> {

    private String fundamentalQuery = "SELECT * FROM ";
    private final SqlQueryInterpreter sqlQueryInterpreter = new SqlQueryInterpreter();

    private final Class<O> clazz;

    private final SqlQueryWrapper queries;
    private final SqlAdapterRegistry sqlAdapterRegistry;
    private  final SqlResponse sqlResponse;
    private final String table;
    private final String database;

    public SqlObjectRepository(Class<O> clazz,
                               SqlQueryWrapper queries,
                               SqlAdapterRegistry sqlAdapterRegistry,
                               String table,
                               String database) {
        this.clazz = clazz;
        this.queries = queries;
        this.sqlAdapterRegistry = sqlAdapterRegistry;
        this.table = table;
        this.database = database;

        sqlResponse = new SqlResponse(sqlAdapterRegistry);
        fundamentalQuery = fundamentalQuery.concat(table);
    }

    @Override
    public void save(O object) {
        delete(object);

        queries.simpleQuery("USE " + database + ";");

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(
                        "INSERT INTO "
                ).append(table)
                .append(" (");

        SqlObjectAdapter<O>
                sqlObjectAdapter = sqlAdapterRegistry.get(object.getClass());

        QueryStatement queryStatement =
                sqlObjectAdapter.write(object);

        for (FieldStatement<?> field : queryStatement.fields()) {
            queryBuilder.append(field.getName())
                    .append(",");
        }

        queryBuilder.deleteCharAt(
                queryBuilder.length() - 1
        );

        queryBuilder.append(") VALUES (");


        for (int i = 0; i < queryStatement.fields().size(); i++) {
            queryBuilder.append("?");

            if (i < queryStatement.fields().size()) {
                queryBuilder.append(",");
            }
        }

        queryBuilder.deleteCharAt(
                queryBuilder.length() - 1
        );

        queryBuilder.append(")");

        queries.updateQuery(
                queryStatement, queryBuilder.toString()
        );

    }

    @Override
    public void delete(O object) {

        System.out.println("USE " + database + ";");
        queries.simpleQuery("USE " + database + ";");

        String deleteQuery = "DELETE FROM "
                + table + " WHERE id = ?";

        QueryStatement queryStatement = QueryStatement.create()
                .insert(FieldStatement.of("id", object.getId(), String.class));

        System.out.println("Size: " + queryStatement.fields().size());

        System.out.println("try deleting");
        queries.updateQuery(
              queryStatement,
                deleteQuery
        );

    }


    @Override
    public boolean exists(O object) {
        ResultSet resultSet = queries.preparedQuery(
                QueryStatement.create()
                        .insert(FieldStatement.of("id", object.getId(), String.class)),
                fundamentalQuery.concat("WHERE id = ?")
        );

        try {
            return resultSet.first();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public CompletableFuture<O> query(CompoundQuery queries) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AAA");
            this.queries.simpleQuery("USE " + database + ";");
            String conditions = sqlQueryInterpreter.interpret(queries);
            String query = fundamentalQuery + conditions;

            System.out.println("Query " + query);

            ResultSet resultSet =
                    this.queries.query(query);

            System.out.println("result set " + resultSet);


            return sqlResponse.response(clazz, resultSet);
        });
    }

    @Override
    public O querySync(CompoundQuery queries) {
        String conditions = sqlQueryInterpreter.interpret(queries);
        String query = fundamentalQuery + conditions;

        this.queries.simpleQuery("USE " + database + ";");
        System.out.println("Query " + query);

        ResultSet resultSet =
                this.queries.query(query);

        System.out.println("result set " + resultSet);

        return sqlResponse.response(clazz, resultSet);
    }

    @Override
    public CompletableFuture<List<O>> queryAll(CompoundQuery queries) {
        return CompletableFuture.supplyAsync(() -> {
            String conditions = sqlQueryInterpreter.interpret(queries);
            String query = fundamentalQuery + conditions;

            ResultSet resultSet =
                    this.queries.query(query);

            return sqlResponse.responseAll(clazz, resultSet);
        });
    }
}
