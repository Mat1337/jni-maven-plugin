# set the minimum cmake version that must be used for this project
cmake_minimum_required(VERSION %CMAKE_MINIMUM_VERSION%)

# set the project name and the language that this project uses
project(%PROJECT_NAME% C CXX)

# set the language version
set(CMAKE_CXX_STANDARD %LANGUAGE_VERSION%)

# set project information
set(PROJECT_NAME %PROJECT_NAME%)
set(PROJECT_VERSION %VERSION%)

# find the JNI package required for developing java native applications
find_package(JNI REQUIRED)

# set all the include directories
include_directories(
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_HOME_DIRECTORY}/%INCLUDE_DIRECTORY%
)

# collect all the source files into a list
file(GLOB_RECURSE SOURCE_LIST "${CMAKE_HOME_DIRECTORY}/%SOURCE_DIRECTORY%")

# create the shared library
add_library(${PROJECT_NAME} SHARED ${SOURCE_LIST})

# change the name of the output library to include the version number
set_target_properties(${PROJECT_NAME} PROPERTIES OUTPUT_NAME ${PROJECT_NAME}-${PROJECT_VERSION})

# copy the library into the build directory
add_custom_command(
    TARGET ${PROJECT_NAME} POST_BUILD COMMAND
    ${CMAKE_COMMAND} -E copy_directory $<TARGET_FILE_DIR:${PROJECT_NAME}>
    ${CMAKE_HOME_DIRECTORY}/%BUILD_DIRECTORY%
)