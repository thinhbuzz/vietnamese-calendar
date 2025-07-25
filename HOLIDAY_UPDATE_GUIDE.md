# Holiday Data Update Guide

This guide explains how to update holiday information in the VietNamCalendar app.

## Overview

The app uses a JSON-based holiday data system that supports both local and remote updates. Holiday data is cached for performance and can be updated from a remote server.

## File Structure

```
app/src/main/
├── assets/
│   └── holidays/
│       └── holidays.json  (Default holiday data)
└── java/.../data/
    ├── models/
    │   └── HolidayData.kt  (Data models)
    ├── HolidayDataLoader.kt  (Loading and caching)
    └── HolidayUpdateManager.kt  (Remote updates)
```

## Holiday JSON Format

The holiday data file uses the following structure:

```json
{
  "version": "1.0.0",
  "lastUpdated": "2025-01-24",
  "holidays": {
    "solar": [
      {
        "id": "unique_id",
        "name": "Holiday Name",
        "month": 1,
        "day": 1,
        "description": "Optional description",
        "isPublicHoliday": true,
        "color": "#FF0000"
      }
    ],
    "lunar": [
      {
        "id": "unique_id",
        "name": "Lunar Holiday",
        "lunarMonth": 1,
        "lunarDay": 1,
        "description": "Optional description",
        "isPublicHoliday": false,
        "color": "#FFA500"
      }
    ]
  }
}
```

### Field Descriptions

#### Common Fields
- `id`: Unique identifier for the holiday (required)
- `name`: Display name of the holiday (required)
- `description`: Optional description or additional information
- `isPublicHoliday`: Whether it's an official public holiday (default: false)
- `color`: Hex color code for UI display (default: "#FF0000")

#### Solar Holiday Fields
- `month`: Month number (1-12) (required)
- `day`: Day of month (1-31) (required)

#### Lunar Holiday Fields
- `lunarMonth`: Lunar month number (1-12) (required)
- `lunarDay`: Lunar day number (1-30) (required)

## How to Update Holiday Data

### Method 1: Update Local Assets (For App Updates)

1. Edit the file: `app/src/main/assets/holidays/holidays.json`
2. Update the `version` number (use semantic versioning)
3. Update the `lastUpdated` date
4. Add, modify, or remove holidays as needed
5. Build and release a new app version

### Method 2: Remote Updates (Without App Update)

1. Host the updated JSON file on your server
2. Ensure the file is accessible via HTTPS
3. Update the `version` number in the remote file
4. The app will automatically check for updates every 7 days

#### Setting Up Remote Updates

1. Configure the remote URL in your app:
   ```kotlin
   // In your Application class or initialization code
   val updateManager = HolidayUpdateManager(context)
   
   // Check for updates (automatic)
   val result = updateManager.checkAndUpdateIfNeeded("https://your-server.com/holidays.json")
   
   // Force update (manual)
   val result = updateManager.forceUpdate("https://your-server.com/holidays.json")
   ```

2. The update manager will:
   - Check if 7 days have passed since last check
   - Compare versions between local and remote
   - Download and save new data if version is higher
   - Clear the cache to use new data

## Adding New Holidays

### Adding a Solar Holiday (Fixed Date)

```json
{
  "id": "womens_day",
  "name": "Ngày Phụ nữ Việt Nam",
  "month": 10,
  "day": 20,
  "description": "Ngày Phụ nữ Việt Nam",
  "isPublicHoliday": false,
  "color": "#FF69B4"
}
```

### Adding a Lunar Holiday

```json
{
  "id": "tet_han_thuc",
  "name": "Tết Hàn thực",
  "lunarMonth": 3,
  "lunarDay": 3,
  "description": "Tết Hàn thực",
  "isPublicHoliday": false,
  "color": "#90EE90"
}
```

## Performance Considerations

1. **Caching**: Holiday data is cached per year to minimize calculations
2. **Memory**: The cache uses ConcurrentHashMap for thread safety
3. **Loading**: Data is loaded lazily when first requested
4. **Updates**: Remote checks happen at most once every 7 days

## Testing Your Changes

1. Place your updated JSON in assets folder
2. Clear app data or uninstall/reinstall to test fresh load
3. Check that holidays appear correctly in the calendar
4. Verify lunar holiday conversions are accurate

## Version Management

- Use semantic versioning (MAJOR.MINOR.PATCH)
- Increment PATCH for holiday corrections
- Increment MINOR for new holidays
- Increment MAJOR for format changes

## Troubleshooting

### Holidays Not Showing
1. Check JSON syntax is valid
2. Verify version number is higher than current
3. Check date calculations (especially for lunar holidays)
4. Look for errors in logcat

### Update Not Working
1. Verify remote URL is accessible
2. Check network permissions in manifest
3. Ensure JSON format matches expected structure
4. Check update interval hasn't been modified

## Server Requirements

If hosting remote updates:
- HTTPS is required
- Set proper MIME type: `application/json`
- Enable CORS if needed
- Consider using CDN for global distribution
- Implement versioning in URL for cache control

## Example Update Workflow

1. Download current `holidays.json`
2. Make your changes
3. Update version from "1.0.0" to "1.0.1"
4. Update lastUpdated to current date
5. Validate JSON syntax
6. Upload to server
7. Test with force update in development
8. Monitor for successful updates in production