# Team 670 Robot code

## Overview

This project is designed for The First Robotics Competetion(FRC). It leverages tools such as WPILib, Gradle, and NetworkTables to provide a robust and scalable solution for this year's(2025) game prompt ReefScape.

## Features

- Two cameras for vision allignment through the use of PhotonVision
- Custom Dashboard functionallity intigrated with AdvantageScope for seamless multi app usage

## System Requirements

To run this project, ensure your system meets the following requirements:

- **Operating System**: Windows, macOS, or Linux
- **Java Version**: Java 17
- **Node Js**: Lastest Version of Node JS
- **Gradle Version**: Included in the project (`gradlew` script)
- **WPILib**: Installed and configured
- **Scripts enabled**: Running scripts must be enabled on your device to run the dashboard
- **Hardware**: FRC robot with specifed subsystems in src/main/java/frc/team670/robot/subsystems

## Setup Instructions

1. Clone the repository:

   ```sh
   git clone <repository-url>
   cd <repository-folder>
   ```

2. Deploy code

```sh
 ./gradlew build
 ./gradlew deploy
```

3. build the script language extention for vs code

```sh
   vsce package ./.vscode/extensions/pita-language/
```

4. right click output file and install in vs code then restart vscode
5. run the dashboard

```sh
   ./runDashboard
```

# Mustang Dashboard

The Mustang Dashboard is a custom-built interface designed to monitor and control the robot during operation. It provides real-time feedback and allows for adjustments to be made on the fly through the custom scripting language(.pita).

## Features of the Dashboard

- **Real-Time Data Visualization**: Displays sensor readings, motor outputs, and other critical data in real-time.
- **Customizable Layout**: Widgets can be rearranged to suit the operator's preferences.
- **Control Panel**: Includes buttons and sliders for manual control of subsystems.
- **Logging and Diagnostics**: Captures logs for debugging and performance analysis.
- **NetworkTables Integration**: Seamlessly communicates with the robot to send and receive data.

## Running the Dashboard

To launch the dashboard, ensure the following prerequisites are met:

1. The robot code is deployed and running on the FRC robot.
2. The computer is connected to the same network as the robot.

Run the dashboard using the provided script:

```sh
./runDashboard
```

Once launched, the dashboard will automatically connect to the robot and display the available data streams.

## Customizing the Dashboard

The dashboard's layout can be customized within the gui through the options menu bar at the top or manually through the `init.pita` file

additionally it can be dynamically updated by creating a new .pita file which will run periodicly see documentation in `./dashboard/scripts/docs.pitadoc/`

The dashboard's functionality can be customized by modifying the configuration file located at:

`./dashboard/config.json`

## Troubleshooting

If the dashboard fails to connect or display data:

1. Verify the robot is powered on and connected to the network.
2. Check the NetworkTables configuration and ensure the correct envType in the config.
3. Review the logs for any error messages (Windows Specific).
