package obs;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class User {
    private final int userid;
    private final String uname;
    private final String pwd;
    private final String email;
    private final String address;
    private final String phone;
    private final boolean logged;

    private User(int userid, String uname, String pwd, String email,
                 String address, String phone, boolean logged) {
        this.userid = userid;
        this.uname = Objects.requireNonNull(uname);
        this.pwd = Objects.requireNonNull(pwd);
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.logged = logged;
    }

    public static User unauthenticated(String username, String password) {
        return new User(0, username, password, "", "", "", false);
    }

    public static User authenticated(int userid, String username, String password,
                                      String email, String address, String phone) {
        return new User(userid, username, password, email, address, phone, true);
    }

    private static final Predicate<String> isValidUsername = u -> u != null && u.length() >= 3;
    private static final Predicate<String> isValidPassword = p -> p != null && p.length() >= 6;
    private static final Predicate<String> isValidEmail = e -> e != null && e.contains("@");
    private static final Predicate<String> isValidPhone = ph -> ph != null && ph.matches("\\d{10}");
    private static final Predicate<User> isValidUser = user ->
        isValidUsername.test(user.uname) &&
        isValidPassword.test(user.pwd) &&
        isValidEmail.test(user.email) &&
        isValidPhone.test(user.phone);

    public static final class Result<T> {
        private final Optional<T> value;
        private final Optional<String> error;

        private Result(Optional<T> value, Optional<String> error) {
            this.value = value;
            this.error = error;
        }

        public static <T> Result<T> success(T value) {
            return new Result<>(Optional.of(value), Optional.empty());
        }

        public static <T> Result<T> failure(String error) {
            return new Result<>(Optional.empty(), Optional.of(error));
        }

        public <R> Result<R> map(Function<T, R> f) {
            return value.map(v -> success(f.apply(v)))
                       .orElseGet(() -> failure(error.get()));
        }

        public <R> Result<R> flatMap(Function<T, Result<R>> f) {
            return value.map(f).orElseGet(() -> failure(error.get()));
        }

        public T getOrElse(T defaultValue) {
            return value.orElse(defaultValue);
        }

        @Override
        public String toString() {
            return value.map(v -> "Success: " + v)
                       .orElseGet(() -> "Failure: " + error.get());
        }
    }

    public int getUserid() { return userid; }
    public String getUname() { return uname; }
    public String getPwd() { return pwd; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public boolean isLogged() { return logged; }

    public User withEmail(String newEmail) {
        if (Objects.equals(newEmail, this.email)) return this;
        return new User(userid, uname, pwd, newEmail, address, phone, logged);
    }

    public User withPhone(String newPhone) {
        if (Objects.equals(newPhone, this.phone)) return this;
        return new User(userid, uname, pwd, email, address, newPhone, logged);
    }

    public User withAddress(String newAddress) {
        if (Objects.equals(newAddress, this.address)) return this;
        return new User(userid, uname, pwd, email, newAddress, phone, logged);
    }

    public User withLoggedStatus(boolean status) {
        if (status == this.logged) return this;
        return new User(userid, uname, pwd, email, address, phone, status);
    }

    public User withUserid(int newUserid) {
        if (newUserid == this.userid) return this;
        return new User(newUserid, uname, pwd, email, address, phone, logged);
    }

    public Result<User> validate() {
        if (!isValidUser.test(this)) {
            return Result.failure("Invalid user data");
        }
        return Result.success(this);
    }

    public Result<User> authenticate(UserRepository repo) {
        Objects.requireNonNull(repo);
        return repo.findByUsernameAndPassword(uname, pwd)
            .map(Result::success)
            .orElseGet(() -> Result.failure("Invalid username or password"));
    }

    public Result<User> register(UserRepository repo) {
        return validate()
            .flatMap(user -> repo.findByUsername(uname)
                .map(existing -> Result.failure("Username already exists"))
                .orElseGet(() -> Result.success(user)))
            .flatMap(user -> repo.save(user)
                .map(Result::success)
                .orElseGet(() -> Result.failure("Failed to save user")));
    }

    public Result<User> updateProfile(UserRepository repo, String newEmail, String newPhone) {
        return this.withEmail(newEmail)
            .withPhone(newPhone)
            .validate()
            .flatMap(updatedUser -> repo.update(updatedUser)
                .map(Result::success)
                .orElseGet(() -> Result.failure("Failed to update profile")));
    }

    public User andThen(Function<User, User> operation) {
        Objects.requireNonNull(operation);
        return operation.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userid == user.userid && Objects.equals(uname, user.uname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, uname);
    }

    @Override
    public String toString() {
        return String.format("User{userid=%d, uname='%s', logged=%b}", userid, uname, logged);
    }
}
