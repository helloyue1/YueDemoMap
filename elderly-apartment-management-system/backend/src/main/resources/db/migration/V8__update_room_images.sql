-- 更新房间图片数据，使用实际上传的图片
-- 创建时间: 2026-02-08

-- 更新已有房间的图片路径为实际上传的图片
UPDATE `room` SET `images` = 'rooms/20260208/126722f2fd174e529fc88f7d37baf38d.jpg' WHERE `room_number` = '101';
UPDATE `room` SET `images` = 'rooms/20260208/194b7f76009743d6a5a61d1f4c127d8f.jpg' WHERE `room_number` = '102';
UPDATE `room` SET `images` = 'rooms/20260208/934896329197476f943274018092bca4.jpg' WHERE `room_number` = '103';
UPDATE `room` SET `images` = 'rooms/20260208/981d4502ea6c48788ad2370983b36697.jpg' WHERE `room_number` = '105';
UPDATE `room` SET `images` = 'rooms/20260208/9b7262973cf647e69319b483e6556b60.jpg' WHERE `room_number` = '201';
UPDATE `room` SET `images` = 'rooms/20260208/af9f66892fd44bc68ee72d8665ceeec6.jpg' WHERE `room_number` = '202';
UPDATE `room` SET `images` = 'rooms/20260208/bc3a1787b24b44d9957fce10d1473e3d.jpg' WHERE `room_number` = '203';
UPDATE `room` SET `images` = 'rooms/20260208/e6dcb761b4614e48bab2abbb40f398bd.jpg' WHERE `room_number` = '205';
UPDATE `room` SET `images` = 'rooms/20260208/fa95753b254b4866a9d8cd9db8d98c8b.jpg' WHERE `room_number` = '301';

-- 为多图片房间添加多张图片（以逗号分隔）
UPDATE `room` SET `images` = 'rooms/20260208/126722f2fd174e529fc88f7d37baf38d.jpg,rooms/20260208/194b7f76009743d6a5a61d1f4c127d8f.jpg' WHERE `room_number` = '302';
UPDATE `room` SET `images` = 'rooms/20260208/934896329197476f943274018092bca4.jpg,rooms/20260208/981d4502ea6c48788ad2370983b36697.jpg' WHERE `room_number` = '303';
UPDATE `room` SET `images` = 'rooms/20260208/9b7262973cf647e69319b483e6556b60.jpg,rooms/20260208/af9f66892fd44bc68ee72d8665ceeec6.jpg' WHERE `room_number` = '305';
