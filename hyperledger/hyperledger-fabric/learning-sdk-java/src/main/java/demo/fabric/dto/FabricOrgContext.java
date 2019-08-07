package demo.fabric.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fabric org 관련 context
 */
@Getter
@Setter
@NoArgsConstructor
public class FabricOrgContext {

    private FabricOrgType orgType;
    private String name;
    private String domain;

    private Map<String, FabricUserContext> users = new HashMap<>();
    private Map<String, FabricPeerContext> peers = new HashMap<>();
    private Map<String, FabricOrdererContext> orderers = new HashMap<>();

    @Builder
    public FabricOrgContext(FabricOrgType orgType, String name, String domain) {
        this.orgType = orgType;
        this.name = name;
        this.domain = domain;
    }

    public void addUser(String name, FabricUserContext userContext) {
        synchronized (users) {
            users.put(name, userContext);
        }
    }

    public void addPeer(String name, FabricPeerContext peerContext) {
        synchronized (peers) {
            peers.put(name, peerContext);
        }
    }

    public void addOrderer(String name, FabricOrdererContext ordererContext) {
        synchronized (orderers) {
            orderers.put(name, ordererContext);
        }
    }

    public FabricUserContext getAdmin() {
        synchronized (users) {
            List<FabricUserContext> admin = users.values().stream().filter(user -> user.isAdmin())
                .collect(Collectors.toList());

            if (admin.size() != 1) {
                throw new IllegalStateException("Admin must be one but exist "
                    + admin.size() + " admins.");
            }

            return admin.get(0);
        }
    }

}
