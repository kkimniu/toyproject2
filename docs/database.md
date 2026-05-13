# Database Notes

This document summarizes the current database shape inferred from MyBatis mappers and entity classes.
The project does not currently include a canonical DDL migration system, so schema changes should be applied manually or through a migration tool before deploying.

## Main Relationships

- `members.work_type_id` -> `work_types.work_type_id`
- `rooms.member_id` -> `members.member_id`
- `rooms.room_type_id` -> `room_types.room_type_id`
- `room_image.room_id` -> `rooms.room_id`
- `favorites.member_id` -> `members.member_id`
- `favorites.room_id` -> `rooms.room_id`
- `token_refresh.member_id` -> `members.member_id`
- `member_hobbies.member_id` -> `members.member_id`
- `member_hobbies.hobby_id` -> `hobbies.hobby_id`
- `member_preferences.member_id` -> `members.member_id`
- `member_preferences.preference_id` -> `preferences.preference_id`
- `member_pets.member_id` -> `members.member_id`
- `member_pets.pet_id` -> `pets.pet_id`

## Recommended Constraints

- `members.email` should be unique.
- `favorites(member_id, room_id)` should be unique to prevent duplicate favorites.
- `token_refresh.member_id` should be unique if each member has only one active refresh token row.
- Join tables should be unique by pair:
  - `member_hobbies(member_id, hobby_id)`
  - `member_preferences(member_id, preference_id)`
  - `member_pets(member_id, pet_id)`

## Recommended Indexes

- `rooms(status, deleted, room_created_at)`
- `rooms(member_id, deleted)`
- `rooms(lat, lng)`
- `rooms(legal_dong)`
- `room_image(room_id, sort_order, image_id)`
- `favorites(member_id, created_at)`
- `favorites(room_id)`
- `token_refresh(member_id)`
- `temp_upload_files(used, created_at)`
- `chat_rooms(room_id, owner_id, partner_id)`

## Required Columns For Current Code

The current code expects these columns to exist.

- `members.gender`: used by main page gender filtering and member profile responses.
- `members.birth_date`: used to calculate age in main page recommendations.
- `members.status`: used by login, JWT authentication, refresh token, recommendation filtering, and future admin workflows.
- `members.banned_until`: exposed in member responses for ban workflow support.
- `room_image.deleted`: used by `RoomImage.xml` in `findAllImageUrls`.

## Current Main Page Data Rule

The main page recommendation API is:

```text
GET /api/members/recommended-roommates
```

It should only return members who have registered active rooms:

- `rooms.deleted = 0`
- `members.deleted = 0`
- `rooms.status IN ('OPEN', 'RESERVED')`

Search filters currently backed by real room data:

- `region`: matches `rooms.address` or `rooms.legal_dong`
- `budget`: filters by `rooms.monthly_rent <= budget`
- `gender`: filters by `members.gender`

## Chat Room Rule

The main page message button uses:

```text
POST /api/chat/rooms
```

The API returns an existing chat room for the same room/member pair or creates one.
`chat_rooms` should have an index or unique key on `room_id`, `owner_id`, and `partner_id` to prevent duplicates.

Filter groups such as hobbies, preferences, pets, and work types are loaded from:

```text
GET /api/members/form-codes
```
