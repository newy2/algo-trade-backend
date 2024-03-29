package com.newy.algotrade.domain.study.library.ta4j

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ta4j.core.BaseTradingRecord
import org.ta4j.core.Trade.TradeType
import org.ta4j.core.TradingRecord
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BaseTradingRecordTest {
    private lateinit var record: TradingRecord

    @BeforeEach
    fun setUp() {
        record = BaseTradingRecord()
    }

    @Test
    fun `BaseTradingRecord 초기 상태 확인`() {
        assertEquals(0, record.positionCount)
        assertNull(record.lastPosition)
        record.currentPosition.run {
            assertTrue(isNew)
            assertNull(entry)
            assertNull(exit)
        }
    }

    @Test
    fun `포지션을 오픈(enter)하는 방법`() {
        val entryIndex = 2

        val isEntered = record.enter(entryIndex)

        assertTrue(isEntered)
        assertEquals(0, record.positionCount, "currentPosition 이 완료되지 않아서 0")
        assertNull(record.lastPosition, "currentPosition 이 완료되지 않아서 null")
        record.currentPosition.run {
            assertTrue(isOpened, "포지션 진행중")
            entry.run {
                assertEquals(entryIndex, index)
                assertEquals(TradeType.BUY, type)
                assertTrue(isBuy)
            }
            assertNull(exit, "포지션이 종료되지 않아서 null")
        }
    }

    @Test
    fun `포지션 종료(exit)하는 방법`() {
        val entryIndex = 2
        val exitIndex = 3

        record.enter(entryIndex)
        val isExited = record.exit(exitIndex)

        assertTrue(isExited)
        assertEquals(1, record.positionCount, "완료된 positionCount 업데이트 됨")
        record.lastPosition.run {
            assertTrue(isClosed, "포지션 완료됨")
            entry.run {
                assertEquals(entryIndex, index)
                assertEquals(TradeType.BUY, type)
                assertTrue(isBuy)
            }
            exit.run {
                assertEquals(exitIndex, index)
                assertEquals(TradeType.SELL, type)
                assertTrue(isSell)
            }
        }
        record.currentPosition.run {
            assertTrue(isNew, "currentPosition 초기화")
            assertNull(entry, "currentPosition 초기화")
            assertNull(exit, "currentPosition 초기화")
        }
    }

    @Test
    fun `포지션을 오픈한 index 보다 낮은 index 로 종료할 수 없다`() {
        record.enter(2)

        assertThrows<IllegalStateException>("거래를 시작한 index 보다 낮은 index 로 종료할 수 없다") {
            record.exit(1)
        }
    }

    @Test
    fun `현재 포지션이 종료 되기 전에 재진입 할 수 없다`() {
        assertTrue(record.enter(2))
        assertFalse(record.enter(2), "같은 index 를 사용한 경우")
        assertFalse(record.enter(3), "다른 index 를 사용한 경우")
    }

    @Test
    fun `포지션을 오픈하지 않고, 종료할 수 없다`() {
        assertFalse(record.exit(2))
    }

    @Test
    fun `포지션 종료 후에 같은 index 로 오픈 수 있다`() {
        record.enter(2)
        record.exit(2)

        val sameIndex = 2
        val isEntered = record.enter(sameIndex)

        assertTrue(isEntered)
        assertEquals(1, record.positionCount)
        record.currentPosition.run {
            assertTrue(isOpened)
            assertEquals(sameIndex, entry.index)
            assertNull(exit)
        }
    }

    @Test
    fun `포지션 종료 후 과거 index 로 오픈할 수 있다`() {
        record.enter(2)
        record.exit(2)

        val beforeIndex = 1
        val isEntered = record.enter(beforeIndex)

        assertTrue(isEntered)
        assertEquals(1, record.positionCount)
        record.currentPosition.run {
            assertTrue(isOpened)
            assertEquals(beforeIndex, entry.index)
            assertNull(exit)
        }
    }
}

class ShortPositionTradeTest {
    @Test
    fun `생성자 별 거래 시작 포지션 확인`() {
        assertEquals(TradeType.BUY, BaseTradingRecord().startingType, "기본 생성자는 롱 표지션")
        assertEquals(TradeType.BUY, BaseTradingRecord(TradeType.BUY).startingType)
        assertEquals(TradeType.SELL, BaseTradingRecord(TradeType.SELL).startingType)
    }

    @Test
    fun `숏포지션 거래진입, 거래종료 테스트`() {
        val record = BaseTradingRecord(TradeType.SELL)

        val isEntered = record.enter(0)
        val isExited = record.exit(1)

        assertTrue(isEntered)
        assertTrue(isExited)
        record.lastPosition.run {
            entry.run {
                assertEquals(0, index)
                assertEquals(TradeType.SELL, type)
                assertTrue(isSell)
            }
            exit.run {
                assertEquals(1, index)
                assertEquals(TradeType.BUY, type)
                assertTrue(isBuy)
            }
        }
    }
}