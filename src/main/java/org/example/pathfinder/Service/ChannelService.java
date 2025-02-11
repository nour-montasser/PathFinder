package org.example.pathfinder.Service;

import org.example.pathfinder.Model.Channel;
import org.example.pathfinder.App.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChannelService {
    private final Connection connection;

    public ChannelService() {
        this.connection = DatabaseConnection.getInstance().getCnx();
    }

    // Add a new channel


}
