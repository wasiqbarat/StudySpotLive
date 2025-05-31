# StudySpot Live

A real-time Android application that allows students to view and update the current busyness status of predefined study locations using Firebase.

## Features

- View a list of study locations with their current status
- Real-time updates when any user changes a location's status
- Simple status categories: Empty, Getting Full, and Packed
- Color-coded status indicators (Green, Yellow, Red)
- Anonymous authentication for simple user access

## Firebase Setup

Before running the app, make sure your Firebase project is set up correctly:

1. Ensure you have created a Firebase project with the Android app registered
2. The `google-services.json` file should be placed in the app directory
3. Enable Anonymous Authentication in Firebase Authentication
4. Set up Firestore Database in Test Mode (for simplicity in this MVP)

### Firestore Data Structure

Create a collection named `study_spots` with the following document structure:

```
study_spots (Collection)
|
├── document_id (Auto-generated or custom)
│   ├── spotName: String (e.g. "Library - Main Area")
│   ├── currentStatus: String (e.g. "Empty", "Getting Full", "Packed")
│   └── lastUpdated: Timestamp
|
├── document_id (Another spot)
│   ├── ...
```

## Initial Data Setup

Use the Firebase Console to add initial study spots. Here's an example:

1. Go to the Firebase Console > Firestore Database
2. Click "Start Collection" and enter "study_spots" as the Collection ID
3. Add a document with the following fields:
   - spotName (Type: String): "Library - Main Area"
   - currentStatus (Type: String): "Empty"
   - lastUpdated (Type: Timestamp): Click the clock icon to set current time

4. Add more study spots as needed following the same pattern

## How the App Works

1. When the app starts, it anonymously authenticates the user with Firebase
2. It then establishes a real-time connection to Firestore to fetch study spots
3. Users can view the list of spots with their current status and last update time
4. When a user taps a spot, they can update its status to Empty, Getting Full, or Packed
5. The change is immediately visible to all other users of the app

## Testing the App

To test the real-time functionality:

1. Install the app on two devices (or run two emulators)
2. On Device 1, tap a study spot and change its status
3. Observe that Device 2 automatically updates with the new status without any manual refresh

## Implementation Details

The app uses:
- Jetpack Compose for the UI
- MVVM architecture with ViewModel and Repository pattern
- Kotlin Coroutines and Flows for asynchronous operations
- Firebase Firestore for real-time data storage
- Firebase Authentication for anonymous user authentication

## Troubleshooting

- If the app doesn't show any study spots, check that your Firestore collection is set up correctly and contains documents
- Ensure that you've enabled anonymous authentication in your Firebase project
- Verify that the google-services.json file is correctly placed in the app directory
