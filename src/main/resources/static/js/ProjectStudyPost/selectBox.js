// List of select elements with their corresponding options and selectBox IDs
const selects = [
    { boxId: 'selectBoxPosition', optionsId: 'optionsPosition', isSingle: false },
    { boxId: 'selectBoxPeople', optionsId: 'optionsPeople', isSingle: true },
    { boxId: 'selectBoxDuration', optionsId: 'optionsDuration', isSingle: true },
    { boxId: 'selectBoxProjectMode', optionsId: 'optionsMode', isSingle: true },
    { boxId: 'selectBoxRecruitmentDeadline', optionsId: 'optionsRecruitmentDeadline', isSingle: true },
    { boxId: 'selectBoxTechStack', optionsId: 'optionsTechStack', isSingle: false },
    { boxId: 'selectBoxRecruitmentType', optionsId: 'optionsRecruitmentType', isSingle: false },
    { boxId: 'selectBoxContactEmail', optionsId: 'optionsContactEmail', isSingle: false }
];

// Function to create a tag element with div as 'x'
function createTag(text, onDelete) {
    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.textContent = text;

    const deleteBtn = document.createElement('div'); // Changed from span to div
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'x';  // Button style as 'x'
    deleteBtn.onclick = onDelete;

    tag.appendChild(deleteBtn);
    return tag;
}

// Initialize each dropdown selects
selects.forEach(({ boxId, optionsId, isSingle }) => {
    const selectBox = document.getElementById(boxId);
    const optionsBox = document.getElementById(optionsId);
    const selectedItems = isSingle ? null : new Set(); // Single-select or multi-select
    let selectedItem = isSingle ? null : null; // Single select item

    // Function to update the selected items display
    function updateSelectedItemsDisplay() {
        // Clear previous tags
        selectBox.querySelectorAll('.tag').forEach(tag => tag.remove());

        // Display selected item (single selection) or multiple selected items (multi-selection)
        if (isSingle) {
            if (selectedItem) {
                const tag = createTag(selectedItem, () => {
                    selectedItem = null;
                    updateSelectedItemsDisplay();
                });
                selectBox.appendChild(tag);
            }
        } else {
            selectedItems.forEach(item => {
                const tag = createTag(item, () => {
                    selectedItems.delete(item);
                    updateSelectedItemsDisplay();
                });
                selectBox.appendChild(tag);
            });
        }

        // Update placeholder visibility
        const placeholder = selectBox.querySelector('.placeholder');
        if (isSingle) {
            placeholder.style.display = selectedItem ? 'none' : 'block';
        } else {
            placeholder.style.display = selectedItems.size > 0 ? 'none' : 'block';
        }
    }

    // Toggle dropdown on selectBox click
    selectBox.addEventListener('click', () => {
        const isExpanded = selectBox.getAttribute('aria-expanded') === 'true';
        selectBox.setAttribute('aria-expanded', !isExpanded);
        optionsBox.style.display = isExpanded ? 'none' : 'block';
    });

    // Add click listeners to each option
    optionsBox.querySelectorAll('.option').forEach(option => {
        option.addEventListener('click', (e) => {
            const selectedText = e.target.textContent;

            if (isSingle) {
                // Single select: Set or unset the selected item
                if (selectedItem !== selectedText) {
                    selectedItem = selectedText;
                } else {
                    selectedItem = null;
                }
            } else {
                // Multi-select: Add or remove items from the selection
                if (selectedItems.has(selectedText)) {
                    selectedItems.delete(selectedText);
                } else {
                    selectedItems.add(selectedText);
                }
            }

            // Update the display
            updateSelectedItemsDisplay();

            // Close dropdown after selection
            optionsBox.style.display = 'none';
            selectBox.setAttribute('aria-expanded', 'false');
        });
    });
});

// Close dropdown if clicked outside
document.addEventListener('click', (event) => {
    selects.forEach(({ boxId, optionsId }) => {
        const selectBox = document.getElementById(boxId);
        const optionsBox = document.getElementById(optionsId);

        if (!selectBox.contains(event.target) && !optionsBox.contains(event.target)) {
            selectBox.setAttribute('aria-expanded', 'false');
            optionsBox.style.display = 'none';
        }
    });
});