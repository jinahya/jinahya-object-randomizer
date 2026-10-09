package com.github.jinahya.object.randomizer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that nested paths of {@link AbstractObjectRandomizer#excludedPaths} are honored, alike, by every flavor.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class AbstractObjectRandomizer_NestedPath_Test {

    /**
     * The value {@link Address} assigns to its {@code country} at construction.
     */
    static final String COUNTRY = "KR";

    /**
     * The value {@link Address} assigns to its {@code number} at construction.
     */
    static final int NUMBER = 7;

    /**
     * An enum whose constants carry state, which no reset may touch, for a constant is shared by the whole program.
     */
    public enum Status {

        ACTIVE("active"),

        INACTIVE("inactive");

        Status(final String label) {
            this.label = label;
        }

        private final String label;

        public String getLabel() {
            return label;
        }
    }

    public static class Geo {

        private String lat;

        private String lng;

        public String getLat() {
            return lat;
        }

        public void setLat(final String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(final String lng) {
            this.lng = lng;
        }
    }

    /**
     * A superclass which declares a slot {@link Address} inherits.
     */
    public static class BaseAddress {

        private String zip;

        public String getZip() {
            return zip;
        }

        public void setZip(final String zip) {
            this.zip = zip;
        }
    }

    public static class Address
            extends BaseAddress {

        private String address1;

        private String address2;

        private String country = COUNTRY;

        private int number = NUMBER;

        private Geo geo;

        public int getNumber() {
            return number;
        }

        public void setNumber(final int number) {
            this.number = number;
        }

        public Geo getGeo() {
            return geo;
        }

        public void setGeo(final Geo geo) {
            this.geo = geo;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(final String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(final String address2) {
            this.address2 = address2;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(final String country) {
            this.country = country;
        }
    }

    public static class User {

        private String address1; // the same name as a slot of Address, at the top level

        private Address address;

        private List<Address> addresses;

        private Address[] addressArray;

        private Map<String, Address> addressMap;

        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(final Status status) {
            this.status = status;
        }

        public Address[] getAddressArray() {
            return addressArray;
        }

        public void setAddressArray(final Address[] addressArray) {
            this.addressArray = addressArray;
        }

        public Map<String, Address> getAddressMap() {
            return addressMap;
        }

        public void setAddressMap(final Map<String, Address> addressMap) {
            this.addressMap = addressMap;
        }

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(final String address1) {
            this.address1 = address1;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(final Address address) {
            this.address = address;
        }

        public List<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(final List<Address> addresses) {
            this.addresses = addresses;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The number of instances drawn per case; more than one, since Fixture Monkey may sample an empty list.
     */
    private static final int DRAWS = 16;

    static Stream<Arguments> flavors() {
        return Stream.of(
                Arguments.of("PODAM", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new PodamObjectRandomizer<>(User.class, v) {
                        }),
                Arguments.of("Instancio", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new InstancioObjectRandomizer<>(User.class, v) {
                        }),
                Arguments.of("Fixture Monkey", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new FixtureMonkeyObjectRandomizer<>(User.class, v) {
                        }),
                Arguments.of("Easy Random", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new EasyRandomObjectRandomizer<>(User.class, v) {
                        })
        );
    }

    private static List<User> draw(final Function<List<String>, ObjectRandomizer<User>> flavor,
                                   final List<String> excludedPaths) {
        final var randomizer = flavor.apply(excludedPaths);
        return IntStream.range(0, DRAWS).mapToObj(i -> randomizer.get()).toList();
    }

    @DisplayName("a nested path excludes the slot of the association, and that slot only")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ExcludedOnTheAssociation(final String name,
                                         final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address.address1"))) {
            assertThat(user.getAddress()).as("the association is randomized").isNotNull();
            assertThat(user.getAddress().getAddress1()).as("the excluded nested slot").isNull();
            assertThat(user.getAddress().getAddress2()).as("a sibling of the excluded slot").isNotNull();
            assertThat(user.getAddress1()).as("a top-level slot of the same name").isNotNull();
        }
    }

    @DisplayName("a nested path reaches every element of a collection")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ExcludedOnEveryElement(final String name,
                                       final Function<List<String>, ObjectRandomizer<User>> flavor) {
        final var elements = draw(flavor, List.of("addresses.address1")).stream()
                .flatMap(u -> u.getAddresses().stream())
                .toList();
        assertThat(elements).as("elements, across all draws").isNotEmpty().allSatisfy(e -> {
            assertThat(e.getAddress1()).as("the excluded slot of an element").isNull();
            assertThat(e.getAddress2()).as("a sibling of the excluded slot").isNotNull();
        });
    }

    @DisplayName("a nested path reaches every element of an array, and every value of a map")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ExcludedOnEveryArrayElementAndMapValue(final String name,
                                                       final Function<List<String>, ObjectRandomizer<User>> flavor) {
        final var users = draw(flavor, List.of("addressArray.address1", "addressMap.address1"));
        final var elements = users.stream().flatMap(u -> Arrays.stream(u.getAddressArray())).toList();
        final var values = users.stream().flatMap(u -> u.getAddressMap().values().stream()).toList();
        for (final var e : List.of(elements, values)) {
            assertThat(e).as("elements, across all draws").isNotEmpty().allSatisfy(v -> {
                assertThat(v.getAddress1()).as("the excluded slot of an element").isNull();
                assertThat(v.getAddress2()).as("a sibling of the excluded slot").isNotNull();
            });
        }
    }

    @DisplayName("a nested path reaches more than one level down")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ExcludedThreeLevelsDown(final String name,
                                        final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address.geo.lat"))) {
            assertThat(user.getAddress().getGeo()).as("the association of the association").isNotNull();
            assertThat(user.getAddress().getGeo().getLat()).as("the excluded slot").isNull();
            assertThat(user.getAddress().getGeo().getLng()).as("a sibling of the excluded slot").isNotNull();
        }
    }

    @DisplayName("a nested path names a slot which the owner inherits")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ExcludedWhenInherited(final String name,
                                      final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address.zip"))) {
            assertThat(user.getAddress().getZip()).as("the excluded, inherited, slot").isNull();
            assertThat(user.getAddress().getAddress1()).as("a slot declared by the owner itself").isNotNull();
        }
    }

    @DisplayName("a nested slot is left at what a freshly constructed owner carries, a primitive included")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_KeepsWhatTheConstructorAssigned(final String name,
                                                final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address.country", "address.number"))) {
            assertThat(user.getAddress().getCountry()).isEqualTo(COUNTRY);
            assertThat(user.getAddress().getNumber()).isEqualTo(NUMBER);
        }
    }

    @DisplayName("a simple path excludes a slot of the target, and not a slot of the same name below it")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void simple_DoesNotReachBelowTheTarget(final String name,
                                           final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address1"))) {
            assertThat(user.getAddress1()).as("the excluded slot of the target").isNull();
            assertThat(user.getAddress().getAddress1()).as("a slot of the same name, of an association").isNotNull();
        }
    }

    @DisplayName("a nested path under an excluded slot is pruned, and the slot is still excluded")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_UnderAnExcludedSlot(final String name,
                                    final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address", "address.address1", "addresses.address1"))) {
            assertThat(user.getAddress()).as("the excluded slot, its nested path notwithstanding").isNull();
            assertThat(user.getAddress1()).as("a slot excluded by neither path").isNotNull();
        }
    }

    @DisplayName("a nested path which names nothing is not an error")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_NamingNothing(final String name,
                              final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("address.nothing", "nothing.address1", "address1.length"))) {
            assertThat(user.getAddress().getAddress1()).isNotNull();
            assertThat(user.getAddress1()).isNotNull();
        }
    }

    @DisplayName("a nested path through an enum constant leaves every constant as it is")
    @MethodSource("flavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_ThroughAnEnumConstant(final String name,
                                      final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("status.label"))) {
            assertThat(user.getStatus()).as("the enum slot itself is randomized").isNotNull();
        }
        assertThat(Status.ACTIVE.getLabel()).isEqualTo("active");
        assertThat(Status.INACTIVE.getLabel()).isEqualTo("inactive");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A subclass of {@link User}, which {@link AbstractObjectRandomizer#newTargetInstance()} may return.
     */
    public static class UserSub
            extends User {

        private Address secondary;

        public Address getSecondary() {
            return secondary;
        }

        public void setSecondary(final Address secondary) {
            this.secondary = secondary;
        }
    }

    static Stream<Arguments> instantiatingFlavors() {
        return Stream.of(
                Arguments.of("PODAM", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new PodamObjectRandomizer<>(User.class, v) {
                            @Override
                            protected User newTargetInstance() {
                                return new UserSub();
                            }
                        }),
                Arguments.of("Instancio", (Function<List<String>, ObjectRandomizer<User>>)
                        v -> new InstancioObjectRandomizer<>(User.class, v) {
                            @Override
                            protected User newTargetInstance() {
                                return new UserSub();
                            }
                        })
        );
    }

    @DisplayName("a nested path reaches a slot which only the subclass newTargetInstance() returns declares")
    @MethodSource("instantiatingFlavors")
    @ParameterizedTest(name = "[{index}] {0}")
    void nested_OnASubclassInstance(final String name,
                                    final Function<List<String>, ObjectRandomizer<User>> flavor) {
        for (final var user : draw(flavor, List.of("secondary.address1", "address.address1"))) {
            assertThat(user).isInstanceOf(UserSub.class);
            final var secondary = ((UserSub) user).getSecondary();
            assertThat(secondary).as("the subclass's own association").isNotNull();
            assertThat(secondary.getAddress1()).as("the excluded slot under it").isNull();
            assertThat(secondary.getAddress2()).as("a sibling of the excluded slot").isNotNull();
            assertThat(user.getAddress().getAddress1()).as("the excluded slot under an inherited one").isNull();
        }
    }

    @DisplayName("Fixture Monkey: a slot set through the builder, and named by a nested path, is excluded all the same")
    @Test
    void nested_WinsOverTheBuilder_OfFixtureMonkey() {
        final var randomizer = new FixtureMonkeyObjectRandomizer<User>(User.class, List.of("address.address1")) {
            @Override
            protected com.navercorp.fixturemonkey.ArbitraryBuilder<User> getArbitraryBuilder() {
                return super.getArbitraryBuilder().set("address.address1", "set").set("address.address2", "set");
            }
        };
        for (int i = 0; i < DRAWS; i++) {
            final var user = randomizer.get();
            assertThat(user.getAddress().getAddress1()).as("set, and excluded").isNull();
            assertThat(user.getAddress().getAddress2()).as("set only").isEqualTo("set");
        }
    }

    @DisplayName("Easy Random: a nested path is excluded even when an override replaces the exclusion policy")
    @Test
    void nested_SurvivesAReplacedPolicy_OfEasyRandom() {
        final var randomizer = new EasyRandomObjectRandomizer<User>(User.class, List.of("address.address1")) {
            @Override
            protected org.jeasy.random.EasyRandomParameters getEasyRandomParameters() {
                return super.getEasyRandomParameters().exclusionPolicy(new org.jeasy.random.DefaultExclusionPolicy());
            }
        };
        for (int i = 0; i < DRAWS; i++) {
            assertThat(randomizer.get().getAddress().getAddress1()).isNull();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    static class Randomizer
            extends AbstractObjectRandomizer<User> {

        Randomizer(final List<String> excludedPaths) {
            super(User.class, excludedPaths);
        }

        @Override
        public User get() {
            throw new UnsupportedOperationException();
        }
    }

    @DisplayName("a path under another one is pruned, segment by segment")
    @Test
    void paths_Pruned() {
        final var randomizer = new Randomizer(List.of(
                "a", "a.b", "a . b . c", "ab.c", "x.y", "x.y.z", "x.yz", "p.q.r"));
        assertThat(randomizer.excludedPaths).containsExactlyInAnyOrder("a", "ab.c", "x.y", "x.yz", "p.q.r");
    }

    @DisplayName("paths are stripped, and deduplicated")
    @Test
    void paths_Normalized() {
        final var randomizer = new Randomizer(List.of(" id ", " address.address1\t", "address.address1"));
        assertThat(randomizer.excludedPaths).containsExactlyInAnyOrder("id", "address.address1");
    }

    @DisplayName("runs of dots and white space separate segments, and the path is rejoined; segments are not verified")
    @Test
    void paths_Normalized_Segments() {
        final var randomizer = new Randomizer(List.of(
                "a..b", " a . b ", "a b", "a\tb", "a\n.b", "a\u00A0b", "a\u3000b", // all of them a.b
                "c.", ".c", " . d . ", "e-f.1g"));
        assertThat(randomizer.excludedPaths).containsExactlyInAnyOrder("a.b", "c", "d", "e-f.1g");
    }

    @DisplayName("a null path is rejected")
    @Test
    void paths_Null_Rejected() {
        assertThatThrownBy(() -> new Randomizer(java.util.Arrays.asList("id", null)))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("a path with no segment which is not blank is rejected")
    @ValueSource(strings = {"", "   ", "\t", "\n", "\u00A0", "\u3000", ".", " . ", "..", ". \t ."})
    @ParameterizedTest
    void paths_Blank_Rejected(final String path) {
        assertThatThrownBy(() -> new Randomizer(List.of(path)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
