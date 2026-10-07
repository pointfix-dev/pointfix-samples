import XCTest

@MainActor final class CaptureUITests: XCTestCase {
    func testLongPressOpensInPlaceComposerAndCancelReturnsToApp() {
        let app = XCUIApplication()
        app.launch()
        let control = app.buttons["checkout.continue"]
        XCTAssertTrue(control.waitForExistence(timeout: 10))
        control.press(forDuration: 1)
        let comment = app.descendants(matching: .any)["pointfix.comment"]
        XCTAssertTrue(comment.waitForExistence(timeout: 5))
        XCTAssertFalse(app.buttons["Send"].isEnabled)
        comment.typeText("Let the label fit")
        let packet = app.descendants(matching: .any)["pointfix.packet"]
        if !packet.exists { app.buttons["pointfix.expert"].tap() } // the expert toggle is remembered between launches
        XCTAssertTrue(packet.waitForExistence(timeout: 5))
        _ = app.staticTexts.containing(NSPredicate(format: "label CONTAINS 'checkout.continue'")).firstMatch.waitForExistence(timeout: 5)
        let screenshot = XCTAttachment(screenshot: app.screenshot())
        screenshot.lifetime = .keepAlways
        add(screenshot)
        app.buttons["Cancel"].tap()
        XCTAssertTrue(control.waitForExistence(timeout: 5))
    }
}
