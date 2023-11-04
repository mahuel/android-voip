# android-voip
Poc Voip app that allows users, who are connected to the same LAN, to make calls. All without a central server. The app makes use of the MVVM design pattern.

## Services
**NetworkMessagingService**
This service starts a ServerSocket instance - this is how app instances on a network are able to listen. This service also sends messages to other instances (e.g. request user details, start call, etc.)

**CallService**
Audio sending and receiving is handled by this service.

## Flow
The app has a very very basic flow, the first screen is where a user enters their display name:

<img src="https://github.com/mahuel/android-voip/assets/15977693/c37d4141-e6ef-43cc-9bad-1d4295f4aa12" width="250" />


The contact screen is where a user can add other contacts, who are on the same LAN. This is done via QR codes - users can start calls from here:

<img src="https://github.com/mahuel/android-voip/assets/15977693/3b206fa0-dd40-42ac-a4f3-87936dd726fe" width="250" />


Finally, a call screen:

<img src="https://github.com/mahuel/android-voip/assets/15977693/4af8c246-cef3-42e0-9fa6-a712dd6ace0b" width="250" />
