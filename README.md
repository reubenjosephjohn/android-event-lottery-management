# Event Lottery Management Android Application

## Overview

The **Event Lottery Management  Android Application** is an Android-based event registration platform that eliminates the need for users to constantly refresh event pages to secure a spot. By using a **lottery system**, the app allows participants to sign up for a waiting list and be randomly selected for events. This ensures that everyone has an equal chance of being selected, without the pressure of being the first to register.

The app integrates **QR code scanning**, **real-time Firebase updates**, and **multi-user roles** to provide a smooth experience for entrants, organizers, and administrators. Whether you're an entrant hoping to join an event, an organizer looking to manage your events and attendees, or an admin overseeing the system, the app provides all the necessary tools.

## Features

- **Lottery-based Event Registration**: Entrants join a waiting list for events and are selected at random when spots become available.
- **QR Code Scanning**: Entrants can scan a QR code to view event details and join the waiting list directly from their mobile devices.
- **Firebase Integration**: Real-time updates on event status, participant selection, and attendee management.
- **User Profiles**: Entrants can create and update their profiles with personal information, profile pictures, and preferences.
- **Geolocation (Optional)**: Event organizers can enable geolocation features to validate user locations when joining the waiting list.
- **Event Management Tools for Organizers**: Organizers can create events, manage waiting lists, and send notifications to selected entrants.
- **Multi-User Roles**: The app supports different user roles (Entrant, Organizer, Admin) with specific permissions tailored to each.
- **Notification System**: Entrants receive notifications when they are selected or when new events are available.

## Installation

### Prerequisites

- **Android Studio** (version 4.x or higher)
- **Java JDK 8** or later
- **Firebase** account for real-time database and authentication configuration
- **Git** for version control

### Setup

1. **Clone the Repository**
   Open your terminal and run the following command to clone the repository to your local machine:

   ```bash
   git clone https://github.com/reubenjosephjohn/android-event-lottery-management.git
   ```

2. **Open the Project in Android Studio**
   - Launch **Android Studio** and open the cloned project.
   
3. **Set Up Firebase**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Create a new project and set up Firebase Authentication and Firestore.
   - Download the **google-services.json** file and place it in the `app/` directory of your project.

4. **Build the Project**
   - Ensure all dependencies are downloaded and sync your project with Gradle.
   - Run the project on your Android device or emulator.

## Usage

### For Entrants:
- **Join Waitlists**: Scan QR codes for event details and register your interest in joining events.
- **Receive Notifications**: Get notified when you're selected for an event or when new events open for registration.
- **Profile Management**: Edit your personal information, upload a profile picture, and update preferences.

### For Organizers:
- **Create Events**: Set up events, specify registration dates, and generate QR codes.
- **Manage Participants**: View waiting lists, randomly select entrants, and notify them about their selection.
- **Send Notifications**: Inform entrants of their status and send updates on event availability.

### For Admins:
- **User and Event Management**: View and manage event details, user profiles, and ensure compliance with app policies.
- **Access Logs**: Review notifications sent to users and track event participation.
