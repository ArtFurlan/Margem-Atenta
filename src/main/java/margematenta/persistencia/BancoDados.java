package margematenta.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class BancoDados {

    private static final String URL = "jdbc:h2:./dados/margem-atenta";

    private BancoDados() {
    }

    static Connection abrirConexao() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }
}