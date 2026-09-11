


// function page(pageNo){
//     window.location.href="index?pageNo="+pageNo;
// }


// <script>
// // 搜索框交互
// document.addEventListener('DOMContentLoaded', () => {
//     const searchInput = document.getElementById('searchInput');
//     const clearBtn = document.querySelector('.search-clear-btn');
//
//     // 清除搜索内容
//     clearBtn.addEventListener('click', () => {
//         searchInput.value = '';
//         searchInput.focus();
//         clearBtn.style.opacity = '0';
//     });
//
//     // 动态显示清除按钮
//     searchInput.addEventListener('input', () => {
//         clearBtn.style.opacity = searchInput.value ? '1' : '0';
//     });
//
//     // 表单验证
//     document.querySelector('form').addEventListener('submit', (e) => {
//         if (!searchInput.value.trim()) {
//             e.preventDefault();
//             searchInput.focus();
//         }
//     });
// });
//
// // 分页功能
// function page(pageNo) {
//     window.location.href = "/pro09_fruit1_4_thymeleaf_Web_exploded/index?pageNo=" + pageNo;
// }
//
// // 添加重置功能
// function resetSearch() {
//     console.log("重置函数被调用"); // 添加调试日志
//     // 清空搜索框
//     document.getElementById("searchInput").value = "";
//     // 带上reset操作参数
//     window.location.href = "/pro09_fruit1_4_thymeleaf_Web_exploded/index?oper=reset";
// }
// </script>

document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const clearBtn = document.getElementById('clearSearch');

    console.log('clearBtn:', clearBtn); // 检查是否获取到按钮

    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            console.log('按钮被点击'); // 检查点击事件是否触发
            searchInput.value = '';
            //window.location.href = '/pro09_fruit1_4_thymeleaf_Web_exploded/index?oper=clear';
        });
    } else {
        console.log('未找到清除按钮');
    }
});
    }

    // 监听输入框变化
    if (searchInput) {
        searchInput.addEventListener('input', () => {
            if (clearBtn) {
                clearBtn.style.opacity = searchInput.value ? '1' : '0';
            }
        });
    }
});

// 分页功能
function page(pageNo) {
    window.location.href = `/pro09_fruit1_4_thymeleaf_Web_exploded/index?pageNo=${pageNo}`;
}

