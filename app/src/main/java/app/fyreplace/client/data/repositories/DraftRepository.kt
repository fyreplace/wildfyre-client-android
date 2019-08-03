package app.fyreplace.client.data.repositories

import app.fyreplace.client.data.Services
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DraftRepository {
    suspend fun getDrafts(offset: Int, size: Int) = withContext(Dispatchers.IO) {
        Services.webService.getDrafts(
            AuthRepository.authToken,
            AreaRepository.preferredAreaName,
            size,
            offset
        )
    }
}
