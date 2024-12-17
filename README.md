# IntentFuzzer
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)](https://android-arsenal.com/api?level=21)
[![license](https://img.shields.io/github/license/balazsgerlei/IntentFuzzer)](https://github.com/balazsgerlei/IntentFuzzer/blob/c587de4b16d9bb80cc1886d8cc72bf43b60e47ba/LICENSE)
[![last commit](https://img.shields.io/github/last-commit/balazsgerlei/IntentFuzzer?color=018786)](https://github.com/balazsgerlei/IntentFuzzer/commits/main)
[![build](https://img.shields.io/github/actions/workflow/status/balazsgerlei/IntentFuzzer/build.yml?branch=main&event=push)](https://github.com/balazsgerlei/IntentFuzzer/actions/workflows/build.yml)

IntentFuzzer is inspired by [this tool](https://www.isecpartners.com/tools/mobile-security/intent-fuzzer.aspx)
developed by [iSECpartners](www.isecpartners.com).

You can choose an application, then either fuzz a single component (Activity, Broadcast Receiver or Service) or all components.

For a single component, just tap an item listed to null fuzz (send and Intent with every property set to null) and long tap to serialize fuzz it (send an Intent with every property set to null except add a Serializable extra with a custom class that will be unknown to the receiving app). Use the "Null Fuzz All" and "Serialize Fuzz All" buttons for all components.
