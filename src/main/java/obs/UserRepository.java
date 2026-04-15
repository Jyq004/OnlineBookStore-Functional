package obs;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByUsername(String username);
    Optional<User> save(User user);
    Optional<User> update(User user);
    boolean delete(int userid);
}
