package webapp;

import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.destination.Destination;

import javax.sql.DataSource;
import org.hsqldb.jdbc.JDBCDataSource;

public class DbSetupConfig {

    public static Destination getDestination() {
        DataSource dataSource = createDataSource();
        return new DataSourceDestination(dataSource);
    }

    private static DataSource createDataSource() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:file:src/main/resources/data/hsqldb/cssdb");
        dataSource.setUser("SA");
        dataSource.setPassword("");
        return dataSource;
    }
}
