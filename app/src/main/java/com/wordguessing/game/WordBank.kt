package com.wordguessing.game

object WordBank {

    /*
     * This keeps compatibility with the existing local GameActivity.
     *
     * GameActivity expects:
     * WordBank.categories.keys
     *
     * and category -> list of words.
     */
    val categories: Map<String, List<String>>

    /*
     * Difficulty-aware word bank.
     *
     * Defaults used by the new game:
     * Easy + Intermediate
     */
    private val wordsByDifficulty =
        mapOf(

            "Animals" to mapOf(

                "Easy" to listOf(
                    "CAT",
                    "DOG",
                    "COW",
                    "PIG",
                    "GOAT",
                    "SHEEP",
                    "HORSE",
                    "LION",
                    "TIGER",
                    "BEAR",
                    "FOX",
                    "WOLF",
                    "DEER",
                    "FROG",
                    "FISH",
                    "BIRD",
                    "DUCK",
                    "CHICKEN",
                    "MOUSE",
                    "RABBIT"
                ),

                "Intermediate" to listOf(
                    "ELEPHANT",
                    "GIRAFFE",
                    "ZEBRA",
                    "MONKEY",
                    "KANGAROO",
                    "PENGUIN",
                    "DOLPHIN",
                    "CAMEL",
                    "GORILLA",
                    "CHEETAH",
                    "LEOPARD",
                    "SQUIRREL",
                    "PARROT",
                    "PEACOCK",
                    "FLAMINGO",
                    "OCTOPUS",
                    "TURTLE",
                    "CROCODILE",
                    "ALLIGATOR",
                    "OSTRICH"
                ),

                "Advanced" to listOf(
                    "CHIMPANZEE",
                    "HIPPOPOTAMUS",
                    "RHINOCEROS",
                    "ORANGUTAN",
                    "PORCUPINE",
                    "RATTLESNAKE",
                    "CHAMELEON",
                    "SALAMANDER",
                    "WOODPECKER",
                    "PLATYPUS",
                    "ARMADILLO",
                    "ANTEATER",
                    "WOLVERINE",
                    "HEDGEHOG",
                    "MEERKAT"
                ),

                "Expert" to listOf(
                    "AXOLOTL",
                    "NARWHAL",
                    "QUOKKA",
                    "CAPYBARA",
                    "WOMBAT",
                    "PANGOLIN",
                    "OKAPI",
                    "TAPIR",
                    "AARDVARK",
                    "KOMODO DRAGON",
                    "MANTIS SHRIMP",
                    "BLUE RINGED OCTOPUS",
                    "SAIGA ANTELOPE",
                    "FENNEC FOX"
                )
            ),

            "Food" to mapOf(

                "Easy" to listOf(
                    "PIZZA",
                    "BURGER",
                    "APPLE",
                    "BANANA",
                    "ORANGE",
                    "RICE",
                    "BREAD",
                    "CHEESE",
                    "EGG",
                    "MILK",
                    "CAKE",
                    "COOKIE",
                    "CANDY",
                    "SOUP",
                    "SALAD",
                    "POTATO",
                    "CARROT",
                    "CORN",
                    "CHICKEN",
                    "PASTA"
                ),

                "Intermediate" to listOf(
                    "PANCAKES",
                    "WAFFLES",
                    "SPAGHETTI",
                    "LASAGNA",
                    "TACOS",
                    "BURRITO",
                    "NACHOS",
                    "SANDWICH",
                    "FRENCH FRIES",
                    "FRIED CHICKEN",
                    "CHEESECAKE",
                    "CHOCOLATE CAKE",
                    "PUMPKIN PIE",
                    "STRAWBERRY",
                    "PINEAPPLE",
                    "PEANUT BUTTER",
                    "POPCORN",
                    "PRETZEL",
                    "YOGURT",
                    "FRUIT SALAD"
                ),

                "Advanced" to listOf(
                    "CHICKEN PARMESAN",
                    "EGGPLANT PARMESAN",
                    "BEEF WELLINGTON",
                    "CHICKEN TIKKA",
                    "FISH AND CHIPS",
                    "STUFFED PEPPERS",
                    "FRENCH TOAST",
                    "CLAM CHOWDER",
                    "CHICKEN NOODLE SOUP",
                    "SHEPHERDS PIE",
                    "BLUEBERRY CHEESECAKE",
                    "CHOCOLATE MOUSSE",
                    "BANANA BREAD",
                    "GARLIC BREAD",
                    "APPLE CRUMBLE"
                ),

                "Expert" to listOf(
                    "RATATOUILLE",
                    "BEEF BOURGUIGNON",
                    "CHICKEN CORDON BLEU",
                    "EGGS BENEDICT",
                    "CREME BRULEE",
                    "PROFITEROLE",
                    "TIRAMISU",
                    "GOULASH",
                    "PAELLA",
                    "BOUILLABAISSE",
                    "GNOCCHI",
                    "BRUSCHETTA",
                    "CARPACCIO",
                    "MOUSSAKA",
                    "SHAKSHUKA"
                )
            ),

            "Places" to mapOf(

                "Easy" to listOf(
                    "CHICAGO",
                    "MIAMI",
                    "BOSTON",
                    "DALLAS",
                    "DENVER",
                    "LONDON",
                    "PARIS",
                    "ROME",
                    "TOKYO",
                    "DELHI",
                    "MUMBAI",
                    "SYDNEY",
                    "TORONTO",
                    "DUBAI",
                    "CAIRO"
                ),

                "Intermediate" to listOf(
                    "NEW YORK",
                    "LOS ANGELES",
                    "SAN FRANCISCO",
                    "LAS VEGAS",
                    "NEW ORLEANS",
                    "WASHINGTON DC",
                    "MEXICO CITY",
                    "NIAGARA FALLS",
                    "CENTRAL PARK",
                    "TIMES SQUARE",
                    "TAJ MAHAL",
                    "DISNEY WORLD",
                    "YELLOWSTONE",
                    "GRAND CANYON",
                    "BIG BEN"
                ),

                "Advanced" to listOf(
                    "GOLDEN GATE BRIDGE",
                    "STATUE OF LIBERTY",
                    "GREAT WALL OF CHINA",
                    "MOUNT RUSHMORE",
                    "MOUNT EVEREST",
                    "VICTORIA FALLS",
                    "BUCKINGHAM PALACE",
                    "HOLLYWOOD SIGN",
                    "EMPIRE STATE BUILDING",
                    "SPACE NEEDLE",
                    "SAHARA DESERT",
                    "ARCTIC OCEAN",
                    "MEDITERRANEAN SEA"
                ),

                "Expert" to listOf(
                    "ANGKOR WAT",
                    "MACHU PICCHU",
                    "PETRA JORDAN",
                    "BOROBUDUR TEMPLE",
                    "GALAPAGOS ISLANDS",
                    "TRANS SIBERIAN RAILWAY",
                    "AMALFI COAST",
                    "MOUNT KILIMANJARO",
                    "ULURU",
                    "SERENGETI NATIONAL PARK",
                    "ANTARCTICA",
                    "PATAGONIA",
                    "FIORDLAND NATIONAL PARK",
                    "EASTER ISLAND"
                )
            ),

            "Sports" to mapOf(

                "Easy" to listOf(
                    "SOCCER",
                    "FOOTBALL",
                    "BASKETBALL",
                    "BASEBALL",
                    "TENNIS",
                    "GOLF",
                    "HOCKEY",
                    "BOXING",
                    "RUNNING",
                    "SWIMMING",
                    "CRICKET",
                    "WRESTLING",
                    "SKIING",
                    "CYCLING",
                    "BOWLING"
                ),

                "Intermediate" to listOf(
                    "VOLLEYBALL",
                    "BADMINTON",
                    "TABLE TENNIS",
                    "SNOWBOARDING",
                    "SURFING",
                    "MARATHON",
                    "ARCHERY",
                    "GYMNASTICS",
                    "WATER POLO",
                    "HORSE RACING",
                    "CAR RACING",
                    "FIGURE SKATING",
                    "BEACH VOLLEYBALL",
                    "AMERICAN FOOTBALL"
                ),

                "Advanced" to listOf(
                    "FORMULA ONE",
                    "PENALTY KICK",
                    "FREE THROW",
                    "THREE POINTER",
                    "TOUCHDOWN",
                    "HOME RUN",
                    "GOALKEEPER",
                    "QUARTERBACK",
                    "CHAMPIONSHIP",
                    "REFEREE",
                    "PITCHER",
                    "TEAM CAPTAIN",
                    "OLYMPIC GAMES",
                    "WORLD CUP"
                ),

                "Expert" to listOf(
                    "DECATHLON",
                    "HEPTATHLON",
                    "POLE VAULT",
                    "HAMMER THROW",
                    "TRIPLE JUMP",
                    "DISCUS THROW",
                    "GRECO ROMAN WRESTLING",
                    "SYNCHRONIZED SWIMMING",
                    "CURLING",
                    "BIATHLON",
                    "LUGE",
                    "SKELETON RACING",
                    "FENCING"
                )
            ),

            "Movies" to mapOf(

                "Easy" to listOf(
                    "SHREK",
                    "FROZEN",
                    "MOANA",
                    "ALADDIN",
                    "MULAN",
                    "CARS",
                    "COCO",
                    "UP",
                    "TOY STORY",
                    "BATMAN",
                    "SUPERMAN",
                    "IRON MAN",
                    "SPIDER MAN",
                    "JUMANJI",
                    "ROCKY"
                ),

                "Intermediate" to listOf(
                    "THE LION KING",
                    "FINDING NEMO",
                    "FINDING DORY",
                    "THE JUNGLE BOOK",
                    "BEAUTY AND THE BEAST",
                    "THE INCREDIBLES",
                    "MONSTERS INC",
                    "RATATOUILLE",
                    "ENCANTO",
                    "HOME ALONE",
                    "JURASSIC PARK",
                    "STAR WARS",
                    "BLACK PANTHER",
                    "THE AVENGERS",
                    "TOP GUN"
                ),

                "Advanced" to listOf(
                    "GUARDIANS OF THE GALAXY",
                    "PIRATES OF THE CARIBBEAN",
                    "MISSION IMPOSSIBLE",
                    "BACK TO THE FUTURE",
                    "MEN IN BLACK",
                    "GHOSTBUSTERS",
                    "THE TERMINATOR",
                    "THE MATRIX",
                    "THE HOBBIT",
                    "LORD OF THE RINGS",
                    "KARATE KID",
                    "SPACE JAM"
                ),

                "Expert" to listOf(
                    "THE SHAWSHANK REDEMPTION",
                    "ETERNAL SUNSHINE",
                    "THE GRAND BUDAPEST HOTEL",
                    "THE SILENCE OF THE LAMBS",
                    "NO COUNTRY FOR OLD MEN",
                    "THE GOOD THE BAD AND THE UGLY",
                    "THE USUAL SUSPECTS",
                    "MEMENTO",
                    "INCEPTION"
                )
            ),

            "Things" to mapOf(

                "Easy" to listOf(
                    "BOOK",
                    "PENCIL",
                    "CHAIR",
                    "TABLE",
                    "BED",
                    "DOOR",
                    "WINDOW",
                    "CLOCK",
                    "BALL",
                    "BAG",
                    "PHONE",
                    "WATCH",
                    "KEY",
                    "CUP",
                    "PLATE",
                    "SPOON",
                    "SHOE",
                    "HAT",
                    "COAT",
                    "BIKE"
                ),

                "Intermediate" to listOf(
                    "TELEPHONE",
                    "COMPUTER",
                    "LAPTOP",
                    "KEYBOARD",
                    "TELEVISION",
                    "CAMERA",
                    "HEADPHONES",
                    "MICROWAVE",
                    "TOASTER",
                    "UMBRELLA",
                    "BACKPACK",
                    "SUITCASE",
                    "WALLET",
                    "SUNGLASSES",
                    "GUITAR",
                    "PILLOW",
                    "BLANKET",
                    "MIRROR",
                    "TOOTHBRUSH"
                ),

                "Advanced" to listOf(
                    "REFRIGERATOR",
                    "WASHING MACHINE",
                    "DISHWASHER",
                    "VACUUM CLEANER",
                    "SCREWDRIVER",
                    "FLASHLIGHT",
                    "TELESCOPE",
                    "KEYCHAIN",
                    "WATER BOTTLE",
                    "COFFEE CUP",
                    "SKATEBOARD",
                    "LADDER",
                    "HAMMER",
                    "MICROSCOPE",
                    "PROJECTOR"
                ),

                "Expert" to listOf(
                    "THERMOMETER",
                    "BAROMETER",
                    "KALEIDOSCOPE",
                    "SEISMOGRAPH",
                    "COMPASS",
                    "METRONOME",
                    "GYROSCOPE",
                    "PERISCOPE",
                    "CALCULATOR",
                    "TYPEWRITER",
                    "PHONOGRAPH",
                    "TELEGRAPH",
                    "BINOCULARS"
                )
            )
        )

    /*
     * Build the old-style category map automatically.
     *
     * This combines all four difficulty levels so the existing
     * local multiplayer GameActivity continues to work.
     */
    init {

        val combined = mutableMapOf<String, List<String>>()

        for ((category, difficultyMap) in wordsByDifficulty) {

            val allWords = mutableListOf<String>()

            for (wordList in difficultyMap.values) {
                allWords.addAll(wordList)
            }

            combined[category] = allWords.distinct()
        }

        categories = combined
    }

    val difficulties = listOf(
        "Easy",
        "Intermediate",
        "Advanced",
        "Expert"
    )

    /*
     * Used by the new Single Player and later
     * by the updated multiplayer screens.
     */
    fun getRandomWord(
        selectedCategories: List<String>,
        selectedDifficulties: List<String>
    ): Pair<String, String> {

        val pool = mutableListOf<Pair<String, String>>()

        for (category in selectedCategories) {

            val difficultyMap =
                wordsByDifficulty[category]
                    ?: continue

            for (difficulty in selectedDifficulties) {

                val wordList =
                    difficultyMap[difficulty]
                        ?: emptyList()

                for (word in wordList) {
                    pool.add(
                        Pair(
                            word,
                            category
                        )
                    )
                }
            }
        }

        /*
         * Safety fallback.
         */
        if (pool.isEmpty()) {
            return Pair(
                "APPLE",
                "Food"
            )
        }

        return pool.random()
    }
}
