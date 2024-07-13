package com.hero.alignlab.client.fcm

import com.hero.alignlab.client.fcm.client.FcmClient
import org.springframework.stereotype.Service

@Service
class PushService(
    private val fcmClient: FcmClient,
) {
}
