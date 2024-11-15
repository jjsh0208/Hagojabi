// List of select elements with their corresponding options and selectBox IDs
const selects = [
    { boxId: 'selectBoxPosition', optionsId: 'optionsPosition', isSingle: false },
    { boxId: 'selectBoxPeople', optionsId: 'optionsPeople', isSingle: true },
    { boxId: 'selectBoxDuration', optionsId: 'optionsDuration', isSingle: true },
    { boxId: 'selectBoxProjectMode', optionsId: 'optionsMode', isSingle: true },
    { boxId: 'selectBoxTechStack', optionsId: 'optionsTechStack', isSingle: false },
    { boxId: 'selectBoxRecruitmentType', optionsId: 'optionsRecruitmentType', isSingle: false },
];

function createTag(text, onDelete) {
    const tag = document.createElement('span');
    tag.className = 'tag';
    tag.textContent = text;

    const deleteBtn = document.createElement('div');
    deleteBtn.className = 'delete-btn';
    deleteBtn.textContent = 'x';
    deleteBtn.onclick = onDelete;

    tag.appendChild(deleteBtn);
    return tag;
}

selects.forEach(({ boxId, optionsId, isSingle }) => {
    const selectBox = document.getElementById(boxId);
    const optionsBox = document.getElementById(optionsId);
    let selectedItem = isSingle ? null : new Set();

    function updateDisplay() {
        selectBox.querySelectorAll('.tag').forEach(tag => tag.remove());

        if (isSingle) {
            if (selectedItem) {
                const tag = createTag(selectedItem, () => {
                    selectedItem = null;
                    updateDisplay();
                });
                selectBox.appendChild(tag);
            }
        } else {
            selectedItem.forEach(item => {
                const tag = createTag(item, () => {
                    selectedItem.delete(item);
                    updateDisplay();
                });
                selectBox.appendChild(tag);
            });
        }

        selectBox.querySelector('.placeholder').style.display = isSingle ? selectedItem ? 'none' : 'block' : selectedItem.size > 0 ? 'none' : 'block';
    }

    selectBox.addEventListener('click', () => {
        const isExpanded = selectBox.getAttribute('aria-expanded') === 'true';
        selectBox.setAttribute('aria-expanded', !isExpanded);
        optionsBox.style.display = isExpanded ? 'none' : 'block';
    });

    optionsBox.querySelectorAll('.option').forEach(option => {
        option.addEventListener('click', (e) => {
            const selectedText = e.target.textContent;

            if (isSingle) {
                selectedItem = selectedItem !== selectedText ? selectedText : null;
            } else {
                selectedItem.has(selectedText) ? selectedItem.delete(selectedText) : selectedItem.add(selectedText);
            }

            updateDisplay();
            optionsBox.style.display = 'none';
            selectBox.setAttribute('aria-expanded', 'false');
        });
    });
});

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


