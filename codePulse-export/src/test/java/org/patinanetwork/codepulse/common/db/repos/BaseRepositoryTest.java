package org.patinanetwork.codepulse.common.db.repos;

import org.patinanetwork.codepulse.common.email.client.codepulse.OfficialCodePulseEmailClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Some clients are slow and are not required to be loaded for database integration tests.
 *
 * <p>All database tests must extend this class.
 */
public class BaseRepositoryTest {

    @MockitoBean
    private OfficialCodePulseEmailClient codepulseEmailClient;
}
