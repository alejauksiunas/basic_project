import React, { useState } from 'react';

function Test() {
    const [isPopupOpen, setPopupOpen] = useState(false);

    const handleButtonClick = () => {
        setPopupOpen(true);
    };

    const handleClosePopup = () => {
        setPopupOpen(false);
    };

    return (
        <div>
            <h1>Test Component</h1>
            <button onClick={handleButtonClick}>Open Popup</button>
            {isPopupOpen && (
                <div>
                    <div>
                        <p>This is the popup content.</p>
                        <button onClick={handleClosePopup}>Close</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Test;