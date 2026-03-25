const express = require('express');
const multer = require('multer');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const { v4: uuidv4 } = require('uuid');

const app = express();
const PORT = 3001;

// 上传目录
const UPLOAD_DIR = path.join(__dirname, 'uploads');
// 后端上传目录（用于兼容已有图片）
const BACKEND_UPLOAD_DIR = path.join(__dirname, '..', 'backend', 'uploads');

// 调试信息
console.log('当前目录:', __dirname);
console.log('图片服务器上传目录:', UPLOAD_DIR);
console.log('后端上传目录:', BACKEND_UPLOAD_DIR);
console.log('后端目录是否存在:', fs.existsSync(BACKEND_UPLOAD_DIR));

// 确保上传目录存在
const ensureDir = (dir) => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
};

ensureDir(UPLOAD_DIR);

// CORS配置 - 允许所有来源访问图片（图片读取需要跨域支持）
app.use(cors({
  origin: '*',
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// 解析表单数据（必须在 multer 之前）
app.use(express.urlencoded({ extended: true }));
app.use(express.json());

// 自定义静态文件服务 - 在两个目录中查找文件
app.use('/uploads', (req, res, next) => {
  const relativePath = req.path.replace(/^\//, ''); // 移除开头的斜杠
  
  // 先在后端目录查找
  const backendPath = path.join(BACKEND_UPLOAD_DIR, relativePath);
  if (fs.existsSync(backendPath)) {
    console.log('从后端目录提供:', relativePath);
    return res.sendFile(backendPath, { headers: { 'Content-Type': getContentType(relativePath) } });
  }
  
  // 再在自己的目录查找
  const localPath = path.join(UPLOAD_DIR, relativePath);
  if (fs.existsSync(localPath)) {
    console.log('从本地目录提供:', relativePath);
    return res.sendFile(localPath, { headers: { 'Content-Type': getContentType(relativePath) } });
  }
  
  console.log('文件不存在:', relativePath);
  console.log('  后端路径:', backendPath);
  console.log('  本地路径:', localPath);
  res.status(404).send('File not found');
});

// 根据文件扩展名获取 Content-Type
function getContentType(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  const mimeTypes = {
    '.jpg': 'image/jpeg',
    '.jpeg': 'image/jpeg',
    '.png': 'image/png',
    '.gif': 'image/gif',
    '.webp': 'image/webp',
    '.svg': 'image/svg+xml'
  };
  return mimeTypes[ext] || 'application/octet-stream';
}

// 存储配置
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const directory = req.body.directory || 'temp';
    const dateDir = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const destDir = path.join(UPLOAD_DIR, directory, dateDir);
    console.log('保存文件到:', destDir);
    ensureDir(destDir);
    cb(null, destDir);
  },
  filename: (req, file, cb) => {
    const uniqueName = uuidv4().replace(/-/g, '') + path.extname(file.originalname);
    console.log('生成文件名:', uniqueName);
    cb(null, uniqueName);
  }
});

// 文件过滤
const fileFilter = (req, file, cb) => {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (allowedTypes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('不支持的图片格式，仅支持 JPEG、PNG、GIF、WebP'), false);
  }
};

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: 5 * 1024 * 1024 // 5MB
  }
});

// 获取服务器基础URL
const getBaseUrl = (req) => {
  const host = req.get('host') || `localhost:${PORT}`;
  const protocol = req.protocol || 'http';
  return `${protocol}://${host}`;
};

// 单文件上传
app.post('/upload', upload.single('file'), (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({
        code: 400,
        message: '没有上传文件'
      });
    }

    // 从实际保存路径生成相对路径
    const actualPath = req.file.path;
    const relativePath = path.relative(UPLOAD_DIR, actualPath).replace(/\\/g, '/');
    const baseUrl = getBaseUrl(req);
    const fullUrl = `${baseUrl}/uploads/${relativePath}`;

    console.log('图片上传成功:', {
      originalName: req.file.originalname,
      directory: req.body.directory,
      actualPath,
      relativePath,
      fullUrl
    });

    res.json({
      code: 200,
      message: '上传成功',
      data: {
        relativePath,
        url: fullUrl,
        filename: req.file.filename,
        size: req.file.size
      }
    });
  } catch (error) {
    console.error('上传失败:', error);
    res.status(500).json({
      code: 500,
      message: '上传失败: ' + error.message
    });
  }
});

// 多文件上传
app.post('/upload/batch', upload.array('files', 10), (req, res) => {
  try {
    if (!req.files || req.files.length === 0) {
      return res.status(400).json({
        code: 400,
        message: '没有上传文件'
      });
    }

    const directory = req.body.directory || 'temp';
    const dateDir = new Date().toISOString().slice(0, 10).replace(/-/g, '');
    const baseUrl = getBaseUrl(req);

    const files = req.files.map(file => {
      const relativePath = `${directory}/${dateDir}/${file.filename}`;
      return {
        relativePath,
        url: `${baseUrl}/uploads/${relativePath}`,
        filename: file.filename,
        size: file.size
      };
    });

    console.log('批量上传成功:', files.length, '个文件');

    res.json({
      code: 200,
      message: '上传成功',
      data: files
    });
  } catch (error) {
    console.error('批量上传失败:', error);
    res.status(500).json({
      code: 500,
      message: '上传失败: ' + error.message
    });
  }
});

// 删除图片
app.delete('/delete', (req, res) => {
  try {
    const { imageUrl } = req.query;
    if (!imageUrl) {
      return res.status(400).json({
        code: 400,
        message: '缺少图片URL参数'
      });
    }

    // 从URL中提取相对路径
    let relativePath = imageUrl;
    if (imageUrl.includes('/uploads/')) {
      relativePath = imageUrl.split('/uploads/')[1];
    }

    const filePath = path.join(UPLOAD_DIR, relativePath);

    // 安全检查：确保文件在uploads目录内
    const resolvedPath = path.resolve(filePath);
    const resolvedUploadDir = path.resolve(UPLOAD_DIR);
    if (!resolvedPath.startsWith(resolvedUploadDir)) {
      return res.status(403).json({
        code: 403,
        message: '非法的文件路径'
      });
    }

    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
      console.log('图片删除成功:', filePath);
      res.json({
        code: 200,
        message: '删除成功',
        data: true
      });
    } else {
      res.status(404).json({
        code: 404,
        message: '文件不存在'
      });
    }
  } catch (error) {
    console.error('删除失败:', error);
    res.status(500).json({
      code: 500,
      message: '删除失败: ' + error.message
    });
  }
});

// 获取图片URL
app.get('/url', (req, res) => {
  const { path: relativePath } = req.query;
  if (!relativePath) {
    return res.status(400).json({
      code: 400,
      message: '缺少路径参数'
    });
  }

  const baseUrl = getBaseUrl(req);
  const fullUrl = `${baseUrl}/uploads/${relativePath}`;
  res.json({
    code: 200,
    message: '成功',
    data: fullUrl
  });
});

// 健康检查
app.get('/health', (req, res) => {
  res.json({
    code: 200,
    message: '服务正常运行',
    data: {
      uploadDir: UPLOAD_DIR,
      port: PORT
    }
  });
});

// 错误处理
app.use((error, req, res, next) => {
  console.error('服务器错误:', error);
  res.status(500).json({
    code: 500,
    message: error.message || '服务器内部错误'
  });
});

app.listen(PORT, () => {
  console.log(`图片服务启动成功！`);
  console.log(`服务地址: http://localhost:${PORT}`);
  console.log(`上传目录: ${UPLOAD_DIR}`);
  console.log('');
  console.log('可用接口:');
  console.log(`  POST http://localhost:${PORT}/upload       - 单文件上传`);
  console.log(`  POST http://localhost:${PORT}/upload/batch - 批量上传`);
  console.log(`  DELETE http://localhost:${PORT}/delete     - 删除图片`);
  console.log(`  GET http://localhost:${PORT}/uploads/*     - 访问图片`);
});
