const https = require('https');
const fs = require('fs');
const path = require('path');
const { v4: uuidv4 } = require('uuid');

// 使用Unsplash API获取免费食物图片
// 由于Unsplash需要API key，我们使用picsum或placeholder图片服务

const foodImages = [
  { name: '小米粥', keyword: 'porridge', ext: 'jpg' },
  { name: '鸡蛋', keyword: 'egg', ext: 'jpg' },
  { name: '豆浆', keyword: 'soy-milk', ext: 'jpg' },
  { name: '馒头', keyword: 'steamed-bun', ext: 'jpg' },
  { name: '清炒时蔬', keyword: 'vegetables', ext: 'jpg' },
  { name: '红烧肉', keyword: 'pork', ext: 'jpg' },
  { name: '清蒸鱼', keyword: 'fish', ext: 'jpg' },
  { name: '番茄炒蛋', keyword: 'tomato-egg', ext: 'jpg' },
  { name: '紫菜蛋花汤', keyword: 'soup', ext: 'jpg' },
  { name: '白米饭', keyword: 'rice', ext: 'jpg' },
  { name: '杂粮粥', keyword: 'congee', ext: 'jpg' },
  { name: '蒸南瓜', keyword: 'pumpkin', ext: 'jpg' },
  { name: '豆腐脑', keyword: 'tofu', ext: 'jpg' },
  { name: '面条', keyword: 'noodles', ext: 'jpg' },
  { name: '馄饨', keyword: 'wonton', ext: 'jpg' }
];

// 使用 Picsum Photos 获取随机食物相关图片
// 或者使用 placeholder 图片服务
const getImageUrl = (index) => {
  // 使用 picsum 获取随机图片，添加随机参数避免缓存
  return `https://picsum.photos/400/300?random=${index + 100}`;
};

// 确保目录存在
const ensureDir = (dir) => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
};

// 下载图片
const downloadImage = (url, filepath) => {
  return new Promise((resolve, reject) => {
    https.get(url, (response) => {
      if (response.statusCode === 200) {
        const fileStream = fs.createWriteStream(filepath);
        response.pipe(fileStream);
        fileStream.on('finish', () => {
          fileStream.close();
          resolve(filepath);
        });
      } else if (response.statusCode === 302 || response.statusCode === 301) {
        // 处理重定向
        downloadImage(response.headers.location, filepath)
          .then(resolve)
          .catch(reject);
      } else {
        reject(new Error(`Failed to download: ${response.statusCode}`));
      }
    }).on('error', reject);
  });
};

// 主函数
async function main() {
  const dateDir = new Date().toISOString().slice(0, 10).replace(/-/g, '');
  const uploadDir = path.join(__dirname, 'uploads', 'food', dateDir);
  ensureDir(uploadDir);

  console.log('开始下载菜品图片...');
  console.log('保存目录:', uploadDir);
  console.log('');

  const results = [];

  for (let i = 0; i < foodImages.length; i++) {
    const food = foodImages[i];
    const filename = uuidv4().replace(/-/g, '') + '.' + food.ext;
    const filepath = path.join(uploadDir, filename);
    const imageUrl = getImageUrl(i);

    try {
      console.log(`[${i + 1}/${foodImages.length}] 下载 ${food.name}...`);
      await downloadImage(imageUrl, filepath);
      const relativePath = `/uploads/food/${dateDir}/${filename}`;
      results.push({
        name: food.name,
        filename: filename,
        path: relativePath,
        fullPath: filepath
      });
      console.log(`  ✓ 成功: ${relativePath}`);
    } catch (error) {
      console.log(`  ✗ 失败: ${error.message}`);
    }

    // 添加延迟避免请求过快
    await new Promise(resolve => setTimeout(resolve, 500));
  }

  console.log('');
  console.log('下载完成！');
  console.log('');
  console.log('图片列表（用于数据库更新）:');
  console.log('----------------------------------------');
  results.forEach((result, index) => {
    console.log(`${index + 1}. ${result.name}`);
    console.log(`   路径: ${result.path}`);
    console.log(`   文件名: ${result.filename}`);
    console.log('');
  });

  // 保存结果到JSON文件
  const resultFile = path.join(uploadDir, 'food-images.json');
  fs.writeFileSync(resultFile, JSON.stringify(results, null, 2));
  console.log(`结果已保存到: ${resultFile}`);
}

main().catch(console.error);
