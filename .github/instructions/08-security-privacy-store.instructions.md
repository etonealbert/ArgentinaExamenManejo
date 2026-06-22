---
applyTo: "**/*.{kt,md,json,xml,plist}"
---

# Security, Privacy, and Store Compliance Instructions

## MVP privacy stance

MVP should minimize data collection:

- no login;
- no analytics unless explicitly approved;
- no backend;
- local progress and exam history only;
- no unnecessary permissions.

## Store/legal screens

Plan settings/about/legal with:

- privacy policy link/text;
- content source attribution;
- non-affiliation disclaimer;
- app version;
- data storage explanation;
- future subscription terms only when subscriptions are implemented.

## Non-affiliation rule

Do not claim or imply that ExamenManejo is an official government app unless formal authorization exists.

## Future backend/security rule

When backend work begins later:

- do not trust client-only subscription state;
- verify App Store / Google Play receipts server-side;
- use HTTPS;
- do not embed sensitive API secrets in client;
- cache entitlement locally with a clear grace-period model.
